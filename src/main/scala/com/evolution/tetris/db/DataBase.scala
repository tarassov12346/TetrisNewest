package com.evolution.tetris.db

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.unsafe.implicits.global

class DataBase {
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:game", "postgres", "mine"
  )

  case class Player(name: String,  score: Int){
    def savePlayerScore(): Int =
      sql"insert into player (name, score) values ($name, $score)".update.run.transact(xa).unsafeRunSync()
  }

 // def find(n: String): Option[Player] =
 //   sql"select name, score from player where name = $n".query[Player].option.transact(xa).unsafeRunSync()

  def collectAllPlayersToListAndSortByScore: List[Player] = {
    sql"select * from player".query[Player].to[List].transact(xa).unsafeRunSync()
  }
}
