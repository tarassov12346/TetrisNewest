package com.evolution.tetris.http

import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import com.comcast.ip4s._
import com.evolution.tetris.main.Main.db
import com.typesafe.config.{Config, ConfigFactory}
import fs2.{Pipe, Stream}
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import org.http4s.{HttpRoutes, _}

import scala.util.Random

class WebSocketServer {

  object WebSocketServer {
    val namesArray: Array[String] = Array("John", "Jack", "Peter", "Kyle", "Lorrain", "Jerry", "Nick", "Paul", "Wolf", "Reiner")
    val http = new TetrisGame(namesArray(Random.nextInt(namesArray.length)))


    private def dbRoute(wsb: WebSocketBuilder2[IO], playerDao: db.PlayerDao): HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root =>
        val showAllFigures: Unit = http.tetris.view.showFallenFiguresAndCurrentFigure(http.tetris.service.fallenFiguresListBuffer, http.tetris.service.currentFigureContainingArrayBuffer)
        val dbPipe: Pipe[IO, WebSocketFrame, WebSocketFrame] = _.collect { case WebSocketFrame.Text(message, _) =>
       //   val showAllFigures: Unit = http.tetris.view.showFallenFiguresAndCurrentFigure(http.tetris.service.fallenFiguresListBuffer, http.tetris.service.currentFigureContainingArrayBuffer)

          if (message.trim.nonEmpty) {
            message.trim.charAt(message.trim.length - 1).toString match {

              case "@" => WebSocketFrame.Text("Current player name is " + http.tetris.playerName + " and his current score is " + http.tetris.view.score.value.toString())

              case "4" =>
                if (http.tetris.service.canMoveTheFigureToLeft) {
                  val moveFigureLeft: Unit = http.tetris.service.currentFigureContainingArrayBuffer(0) = http.tetris.service.currentFigureContainingArrayBuffer(0).moveFigureToLeft()
                  WebSocketFrame.Text("Figure moved left: " + moveFigureLeft.toString + showAllFigures.toString + http.tetris.service.currentFigureContainingArrayBuffer(0).moveFigureToLeft().toString)
                }
                else WebSocketFrame.Text("No further left move possible!")

              case "6" =>
                if (http.tetris.service.canMoveTheFigureToRight) {
                  val moveFigureRight: Unit = http.tetris.service.currentFigureContainingArrayBuffer(0) = http.tetris.service.currentFigureContainingArrayBuffer(0).moveFigureToRight()
                  WebSocketFrame.Text("Figure moved right: " + moveFigureRight.toString + showAllFigures.toString + http.tetris.service.currentFigureContainingArrayBuffer(0).moveFigureToRight().toString)
                }
                else WebSocketFrame.Text("No further right move possible!")

              case "5" =>
                val rotateFigure: Unit = http.tetris.service.currentFigureContainingArrayBuffer(0) = http.tetris.service.currentFigureContainingArrayBuffer(0).rotateFigureClockwise()
                if (http.tetris.service.canRotateTheFigure(true)) {
                  WebSocketFrame.Text("Figure rotated: " + rotateFigure.toString + showAllFigures.toString + http.tetris.service.currentFigureContainingArrayBuffer(0).rotateFigureClockwise().toString)
                }
                else WebSocketFrame.Text("No rotation possible!")

              case "2" =>
                val dropFigure: Unit = http.tetris.service.makeFigureGoDownQuick()
                if (!http.tetris.service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean) {
                  WebSocketFrame.Text("Figure dropped  " + dropFigure.toString + showAllFigures.toString)
                }
                else WebSocketFrame.Text("No drop possible!")

              case "7" =>
                val pauseGame: Unit = http.tetris.service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0) = (!http.tetris.service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean).toString
                WebSocketFrame.Text("Game paused  " + pauseGame.toString + showAllFigures.toString)

              case "9" =>
                if (http.tetris.service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1).toBoolean) {
                  val changeFigure: Unit = http.tetris.service.currentFigureContainingArrayBuffer(0) = http.tetris.service.generateRandomOrBonusFigure()
                  val turnOff: Unit = http.tetris.service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1) = "false"
                  WebSocketFrame.Text("Figure changed  " + changeFigure.toString + turnOff.toString + http.tetris.service.generateRandomOrBonusFigure().toString)
                }
                else WebSocketFrame.Text("No Figure change is allowed for now!")

              case _ =>
                val f = playerDao.from(ConfigFactory.load()).unsafeRunSync().find(message).unsafeRunSync().sortWith((x, y) => x.score > y.score).headOption
                f match {
                  case Some(value) => WebSocketFrame.Text(value.name + "'s BEST score is " + value.score)
                  case None => WebSocketFrame.Text("None!")
                }
            }
          }
          else WebSocketFrame.Text("No signal!")
        }

        for {
          queue <- Queue.unbounded[IO, WebSocketFrame]
          response <- wsb.build(
            receive = _.evalMap(queue.offer),
            send = Stream.repeatEval(queue.take).through(dbPipe),
          )
        } yield response
    }

    private def httpApp(wsb: WebSocketBuilder2[IO], playerDao: db.PlayerDao): HttpApp[IO] = {
      dbRoute(wsb, playerDao)
    }.orNotFound

    def run(config: Config, playerDao: db.PlayerDao): IO[ExitCode] = {
      val hostString = config.getString("myServer.host.value")
      val portString = config.getString("myServer.port.value")
      for {
        _ <- EmberServerBuilder
          .default[IO]
          .withHost(Host.fromString(hostString).get)
          .withPort(Port.fromString(portString).get)
          .withHttpWebSocketApp(httpApp(_, playerDao))
          .build.useForever.both(http.start())
      } yield ExitCode.Success
    }
  }
}
