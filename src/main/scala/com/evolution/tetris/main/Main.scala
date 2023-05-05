package com.evolution.tetris.main

import cats.effect._
import com.evolution.tetris.desktopGame.TetrisDesktopGame

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    IO {
      println("Enter your name:")
      val playerName = scala.io.StdIn.readLine()
      val tetris = TetrisDesktopGame(playerName)
      tetris.main(Array())
    }.as(ExitCode.Success)
}
