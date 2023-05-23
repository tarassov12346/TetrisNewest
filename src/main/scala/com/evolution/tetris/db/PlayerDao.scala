package com.evolution.tetris.db

import cats.effect.IO
import com.evolution.tetris.service.Player
import com.typesafe.config.Config
import doobie._
import doobie.implicits._



trait PlayerDao {

    def savePlayerScore(name: String, score: Int): IO[Int]

    def find(n: String): IO[List[Player]]

    def collectAllPlayersToListAndSortByScore: IO[List[Player]]

  }

  object PlayerDao {

    def from(config: Config): IO[PlayerDao] = IO {
      val driver = config.getString("myDb.driver.value")
      val url = config.getString("myDb.url.value")
      val user = config.getString("myDb.user.value")
      val pass = config.getString("myDb.pass.value")
      val xa = Transactor.fromDriverManager[IO](driver, url, user, pass)
      new PlayerDao {

        override def savePlayerScore(name: String, score: Int): IO[Int] = sql"insert into player (name, score) values ($name, $score)".update.run.transact(xa)

        override def find(n: String): IO[List[Player]] = sql"select name, score from player where name = $n".query[Player].to[List].transact(xa)

        override def collectAllPlayersToListAndSortByScore: IO[List[Player]] = sql"select * from player".query[Player].to[List].transact(xa)
      }
    }
  }
