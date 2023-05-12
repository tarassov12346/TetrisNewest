package com.evolution.tetris.db

import cats.effect.IO
import com.evolution.tetris.service.Player
import com.typesafe.config.Config
import doobie._
import doobie.implicits._

class DataBase {

  trait PlayerDao {

    def from(config:Config): PlayerDao

    def savePlayerScore(name: String, score: Int): IO[Int]

    def find(n: String): IO[List[Player]]

    def collectAllPlayersToListAndSortByScore: IO[List[Player]]

  }

  object PlayerDao extends PlayerDao {

    val xaArray = Array(Transactor.fromDriverManager[IO]("", "", "", ""))

    def from(config:Config):PlayerDao = {
      val driver = config.getString("myDb.driver.value")
      val url = config.getString("myDb.url.value")
      val user = config.getString("myDb.user.value")
      val pass = config.getString("myDb.pass.value")
      xaArray(0) =Transactor.fromDriverManager[IO](driver, url, user, pass)
      this
    }

    def savePlayerScore(name: String, score: Int): IO[Int] =
      sql"insert into player (name, score) values ($name, $score)".update.run.transact(xaArray(0))

    def find(n: String): IO[List[Player]] =
      sql"select name, score from player where name = $n".query[Player].to[List].transact(xaArray(0))

    def collectAllPlayersToListAndSortByScore: IO[List[Player]] =
      sql"select * from player".query[Player].to[List].transact(xaArray(0))
  }
}