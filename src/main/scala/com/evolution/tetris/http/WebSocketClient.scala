package com.evolution.tetris.http

import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s.client.websocket.{WSFrame, WSRequest}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.jdkhttpclient.JdkWSClient

import java.net.http.HttpClient

object WebSocketClient extends IOApp{
  private val uri = uri"ws://localhost:9002/db"

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
            println(s)
            s
          }
          .compile
          .string
      } yield ExitCode.Success
    }
  }

}
