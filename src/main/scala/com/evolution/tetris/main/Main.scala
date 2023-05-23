package com.evolution.tetris.main

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.tetris.db.PlayerDao
import com.evolution.tetris.http.{WebSocketHtmlInBrowser, WebSocketServer}
import com.typesafe.config.ConfigFactory

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- IO(ConfigFactory.load())
      playerDao <- PlayerDao.from(config)
      _ <- WebSocketHtmlInBrowser.getHtml1
      _ <-  new WebSocketServer(playerDao).WebSocketServer.run(config)
    } yield ExitCode.Success
  }
}
