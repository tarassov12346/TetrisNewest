package com.evolution.tetris.main

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.all._
import com.evolution.tetris.db.DataBase
import com.evolution.tetris.desktopGame.TetrisDesktopGame
import org.http4s.client.websocket.{WSFrame, WSRequest}
import org.http4s.implicits._
import org.http4s.jdkhttpclient.JdkWSClient

import java.net.http.HttpClient

object Main extends IOApp {

  private val uri = uri"ws://localhost:9002/echo"

  val db = new DataBase

  private def printLine(string: String = ""): IO[Unit] = IO {
    println(string)
  }

  override def run(args: List[String]): IO[ExitCode] = {

    println("Enter your name:")
    val playerName = scala.io.StdIn.readLine()

    val clientResource = Resource
      .eval(IO(HttpClient.newHttpClient()))
      .flatMap(JdkWSClient[IO](_).connectHighLevel(WSRequest(uri)))

    clientResource.use { client =>
      for {
        _ <- client.send(WSFrame.Text(playerName))
        _ <- client.receiveStream
          .collectFirst { case WSFrame.Text(s, _) =>
            s
          }
          .compile
          .string >>=
          printLine >>=
          (_ => IO(TetrisDesktopGame(playerName).main(Array()))) >>=
          (_ => IO(println("***************\n"+
            playerName +
            "'s Best Result: " +
            db.find(playerName).sortWith((x, y) => x.score > y.score).head))
          )
        _ <- client.receiveStream
          .collectFirst { case WSFrame.Text(s, _) =>
            s
          }.compile.string >>= printLine
      } yield ExitCode.Success
    }
  }
}
