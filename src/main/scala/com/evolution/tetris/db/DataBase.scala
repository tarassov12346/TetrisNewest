package com.evolution.tetris.db

import cats.effect.IO
import com.evolution.tetris.service.Player
import com.typesafe.config.{Config, ConfigFactory}
import doobie._
import doobie.implicits._
import doobie.util.transactor

class DataBase {

  trait PlayerDao {

    def from(config:Config): transactor.Transactor.Aux[IO, Unit]

    def savePlayerScore(name: String, score: Int): IO[Int]

    def find(n: String): IO[List[Player]]

    def collectAllPlayersToListAndSortByScore: IO[List[Player]]

  }

  object PlayerDao extends PlayerDao {

    def from(config:Config) = {
      val driver = config.getString("myDb.driver.value")
      val url = config.getString("myDb.url.value")
      val user = config.getString("myDb.user.value")
      val pass = config.getString("myDb.pass.value")
      Transactor.fromDriverManager[IO](driver, url, user, pass)
    }

    def savePlayerScore(name: String, score: Int): IO[Int] =
      sql"insert into player (name, score) values ($name, $score)".update.run.transact(from(ConfigFactory.load()))

    def find(n: String): IO[List[Player]] =
      sql"select name, score from player where name = $n".query[Player].to[List].transact(from(ConfigFactory.load()))

    def collectAllPlayersToListAndSortByScore: IO[List[Player]] =
      sql"select * from player".query[Player].to[List].transact(from(ConfigFactory.load()))
  }
}