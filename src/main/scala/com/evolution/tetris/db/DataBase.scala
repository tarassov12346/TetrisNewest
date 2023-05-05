package com.evolution.tetris.db

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.typesafe.config.ConfigFactory

class DataBase {
  val driver = ConfigFactory.load().getString("myDb.driver.value")
  val url = ConfigFactory.load().getString("myDb.url.value")
  val user = ConfigFactory.load().getString("myDb.user.value")
  val pass = ConfigFactory.load().getString("myDb.pass.value")
  val xa = Transactor.fromDriverManager[IO](
    driver, url, user, pass
  )

  case class Player(name: String, score: Int) {
    def savePlayerScore(): Int =
      sql"insert into player (name, score) values ($name, $score)".update.run.transact(xa).unsafeRunSync()
  }

  // def find(n: String): Option[Player] =
  //   sql"select name, score from player where name = $n".query[Player].option.transact(xa).unsafeRunSync()

  def collectAllPlayersToListAndSortByScore: List[Player] = {
    sql"select * from player".query[Player].to[List].transact(xa).unsafeRunSync()
  }
}
