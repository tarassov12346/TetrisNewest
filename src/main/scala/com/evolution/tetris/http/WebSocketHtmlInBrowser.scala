package com.evolution.tetris.http

import cats.effect._
import java.net.URI

object WebSocketHtmlInBrowser {
  val uri1= new URI("https://tigoe.github.io/websocket-examples/p5jsClient/")
  val getHtml1: IO[Unit] =IO(java.awt.Desktop.getDesktop.browse(uri1))
  }


