package com.evolution.tetris.main

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.tetris.db.DataBase
import com.evolution.tetris.http.{WebSocketHtmlInBrowser, WebSocketServer}
import com.typesafe.config.ConfigFactory

object Main extends IOApp {
  val wb = new WebSocketServer()
  val db = new DataBase

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- IO(ConfigFactory.load())
      playerDao <- db.PlayerDao.from(config)
      _ <- WebSocketHtmlInBrowser.getHtml1
      _ <- wb.WebSocketServer.run(config, playerDao)
    } yield ExitCode.Success
  }
}
