package com.evolution.tetris.game

object Main {
  def main (args: Array[String]): Unit ={

    println("Enter your name:")
    val playerName = scala.io.StdIn.readLine()

    val tetris = new TetrisGame(playerName)
    tetris.main(Array())
  }
}
