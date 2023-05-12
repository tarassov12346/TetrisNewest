package com.evolution.tetris.http

import cats.effect.IO
import com.evolution.tetris.desktopGame.TetrisDesktopGame

class TetrisGame (playerName: String) {
  val tetris = TetrisDesktopGame(playerName)

  def start(): IO[Unit]= IO {
    tetris.main(Array())
  }
}
