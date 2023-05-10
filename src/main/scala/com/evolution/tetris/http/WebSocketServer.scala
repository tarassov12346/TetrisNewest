package com.evolution.tetris.http

import cats.effect.std.Queue
import cats.effect.{Clock, ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId
import com.comcast.ip4s._
import fs2.{Pipe, Stream}
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import org.http4s.{HttpRoutes, _}

object WebSocketServer extends IOApp {

  // Let's build a WebSocket server using Http4s.
  private def echoRoute(wsb: WebSocketBuilder2[IO]) = HttpRoutes.of[IO] {

    // websocat "ws://localhost:9002/echo"
    case GET -> Root / "echo" =>
      // Pipe is a stream transformation function of type `Stream[F, I] => Stream[F, O]`. In this case
      // `I == O == WebSocketFrame`. So the pipe transforms incoming WebSocket messages from the client to
      // outgoing WebSocket messages to send to the client.
      val echoPipe: Pipe[IO, WebSocketFrame, WebSocketFrame] =
        _.collect { case WebSocketFrame.Text(message, _) =>
          WebSocketFrame.Text(message)
        }.evalMap {
          case WebSocketFrame.Text(message, _) if message.trim == "time" =>
            Clock[IO].realTimeInstant.map(i => WebSocketFrame.Text(i.toString))
          case other =>
            other.pure[IO]
        }

      for {
        // Unbounded queue to store WebSocket messages from the client, which are pending to be processed.
        // For production use bounded queue seems a better choice. Unbounded queue may result in out of
        // memory error, if the client is sending messages quicker than the server can process them.
        queue <- Queue.unbounded[IO, WebSocketFrame]
        response <- wsb.build(
          // Sink, where the incoming WebSocket messages from the client are pushed to.
          receive = _.evalMap(queue.offer),
          // Outgoing stream of WebSocket messages to send to the client.
          send = Stream.repeatEval(queue.take).through(echoPipe),
        )
      } yield response
  }

  private def httpApp(wsb: WebSocketBuilder2[IO]): HttpApp[IO] = {
    echoRoute(wsb)
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"127.0.0.1")
        .withPort(port"9002")
        .withHttpWebSocketApp(httpApp)
        .build
        .useForever
    } yield ExitCode.Success
}
