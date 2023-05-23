package com.evolution.tetris.http

import cats.effect.IO
import com.evolution.tetris.db.PlayerDao
import com.evolution.tetris.desktopGame.TetrisDesktopGame

class TetrisGame (playerName: String, playerDao: PlayerDao) {

  val tetris = TetrisDesktopGame(playerName,playerDao)

  def start(): IO[Unit]= IO {
    tetris.main(Array())
  }
}
