package com.evolution.tetris.main

import com.evolution.tetris.desktopGame.TetrisDesktopGame

object Main {
  def main(args: Array[String]): Unit = {
    println("Enter your name:")
    val playerName = scala.io.StdIn.readLine()
    val tetris = TetrisDesktopGame(playerName)
    tetris.main(Array())
  }
}
