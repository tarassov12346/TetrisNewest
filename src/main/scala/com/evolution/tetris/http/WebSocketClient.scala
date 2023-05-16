package com.evolution.tetris.http

import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO}
import org.http4s.client.websocket.{WSFrame, WSRequest}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.jdkhttpclient.JdkWSClient

import java.net.http.HttpClient

case class WebSocketClient(playerName:String,eventSign:String) {

  private val uri = uri"ws://localhost:8080"

  object WebSocketClient{

    def run(): IO[ExitCode] = {
      val clientResource = Resource
        .eval(IO(HttpClient.newHttpClient()))
        .flatMap(JdkWSClient[IO](_).connectHighLevel(WSRequest(uri)))

      clientResource.use { client =>
        for {
          _ <- client.send(WSFrame.Text(eventSign+playerName))
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




}
