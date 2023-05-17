package com.evolution.tetris.main

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.tetris.db.DataBase
import com.evolution.tetris.http.{TetrisGame, WebSocketHtmlInBrowser, WebSocketServer}
import com.typesafe.config.ConfigFactory

object Main extends IOApp {
  val wb = new WebSocketServer()
  val db = new DataBase

  override def run(args: List[String]): IO[ExitCode] = {
    println("Enter your name:")
    val playerName = scala.io.StdIn.readLine()
    val http = new TetrisGame(playerName)
    for {
      config <- IO(ConfigFactory.load())
      playerDao <- db.PlayerDao.from(config)
      _ <- WebSocketHtmlInBrowser.getHtml1
      _ <- wb.WebSocketServer.run(config, playerDao, http).useForever.both(http.start())
    } yield ExitCode.Success
  }
}
