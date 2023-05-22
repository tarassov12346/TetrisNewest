package com.evolution.tetris.service

import cats.effect.unsafe.implicits.global
import com.evolution.tetris.db.DataBase
import com.evolution.tetris.service
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

final case class ServiceFunctions(playerName: String) {
  val presetsObject = new Presets()
  val tetrisSceneBooleanMatrixArrayBuffer: ArrayBuffer[ArrayBuffer[Boolean]] =
    ArrayBuffer.fill[Boolean](presetsObject.sceneHeight, presetsObject.sceneWidth)(false)
  val fallenFiguresListBuffer = ListBuffer[Figure]()
  val currentFigureContainingArrayBuffer: ArrayBuffer[Figure] = ArrayBuffer.fill[Figure](1)(generateRandomOrBonusFigure())
  val figureSupposedToBeRotatedArrayBuffer =
    ArrayBuffer[Figure](service.Figure(currentFigureContainingArrayBuffer(0).horizontalPosition, currentFigureContainingArrayBuffer(0).verticalPosition, currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.clone(), currentFigureContainingArrayBuffer(0).colorChoiceCode, presetsObject))
  val db = new DataBase
  val scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity: Array[Int] =Array(0,0,0)

  def generateRandomOrBonusFigure(): Figure = {
    presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) match {
      case "no bonus" =>
        val figureShapeRandomPattern = presetsObject.presetFigureShapePatternsSequence(math.abs(Random.nextInt(presetsObject.presetFigureShapePatternsSequence.length)))
        service.Figure(presetsObject.sceneWidth / 2, 0, figureShapeRandomPattern.toArray, scala.util.Random.nextInt(9), presetsObject)
      //Check the previous 2 lines if smth goes wrong!!!!!!!!!!!!!!!!!!!!
      case "drop on one row down" =>
        scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2)-=1
        presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "no bonus"
        presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2) = "true"
        service.Figure(presetsObject.sceneWidth / 2, 0, Array(Array(x = true)), 12, presetsObject)
      case "simple figure" =>
        scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2)-=1
        if (scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2) == 0) presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "no bonus"
        service.Figure(presetsObject.sceneWidth / 2, 0, Array(Array(x = true)), 13, presetsObject)
    }
  }

  def formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore(): Unit = {
    for (i <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.indices) {
      for (j <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(0).indices) {
        tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + i)(currentFigureContainingArrayBuffer(0).horizontalPosition + j) =
          tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + i)(currentFigureContainingArrayBuffer(0).horizontalPosition + j) ||
            currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i)(j)
      }
    }
    fallenFiguresListBuffer.addOne(currentFigureContainingArrayBuffer(0))
    currentFigureContainingArrayBuffer(0) = generateRandomOrBonusFigure()
    scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0)+=5
  }

  def analyzeTheAvailabilityOfBonusesAddToScoreIfTheRowIsFilledAndReduceTheFilledRow(): Unit = {
    for (i <- 0 until presetsObject.sceneHeight) {
      val isRowFilled = !tetrisSceneBooleanMatrixArrayBuffer(i).contains(false)
      if (isRowFilled) {
        scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1)+=1
        if (scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1) % 2 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "simple figure"
          scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2)=
            scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1)/5 +1
        }
        if (scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1) % 3 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1) = "true"
        }
        if (scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1) % 5 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "drop on one row down"
          scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2)=1
        }
        scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0)+=10
        tetrisSceneBooleanMatrixArrayBuffer.remove(i)
        tetrisSceneBooleanMatrixArrayBuffer.prepend(ArrayBuffer.fill(presetsObject.sceneWidth)(false))

        fallenFiguresListBuffer.mapInPlace(fallenFigure => {
          if (fallenFigure.verticalPosition <= i && fallenFigure.verticalPosition + fallenFigure.shapeFormingBooleanMatrix.length >= i)
            service.Figure(fallenFigure.horizontalPosition, fallenFigure.verticalPosition,
              fallenFigure.shapeFormingBooleanMatrix.take(i - fallenFigure.verticalPosition).++(fallenFigure.shapeFormingBooleanMatrix.drop(i - fallenFigure.verticalPosition + 1)),
              fallenFigure.colorChoiceCode, presetsObject)
          else fallenFigure
        })

        fallenFiguresListBuffer.mapInPlace(fallenFigure => {
          if (fallenFigure.verticalPosition <= i) {
            service.Figure(fallenFigure.horizontalPosition, fallenFigure.verticalPosition + 1,
              fallenFigure.shapeFormingBooleanMatrix, fallenFigure.colorChoiceCode, presetsObject)
          } else fallenFigure
        })
      }
    }
  }

  def makeFigureGoDownQuick(): Unit = {
    while (canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue) {
    }
  }

  def resetGame(score: Int): Unit = {
    db.PlayerDao.from(ConfigFactory.load()).unsafeRunSync().savePlayerScore(playerName, score).unsafeRunSync()
    db.PlayerDao.from(ConfigFactory.load()).unsafeRunSync().collectAllPlayersToListAndSortByScore.unsafeRunSync().sortWith((x, y) => x.score > y.score).foreach(player => println(player))
    fallenFiguresListBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.addAll(ArrayBuffer.fill[Boolean](presetsObject.sceneHeight, presetsObject.sceneWidth)(false))
    presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1) = "false"
    presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2) = "false"
    presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "no bonus"
  }

  def canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue: Boolean = {
    val indexOfTheRowUnder = currentFigureContainingArrayBuffer(0).verticalPosition + currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.length
    if (indexOfTheRowUnder == presetsObject.sceneHeight) {
      formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
      false
    }
    else {
      val canTheFigureGoDownCheckListBuffer = ListBuffer[Boolean]()
      for (i <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.indices) {
        for (j <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i).indices) {
          canTheFigureGoDownCheckListBuffer.addOne((currentFigureContainingArrayBuffer(0).horizontalPosition + j) >= presetsObject.sceneWidth ||
            (currentFigureContainingArrayBuffer(0).verticalPosition + 1 + i) >= presetsObject.sceneHeight ||
            currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i)(j) &&
              tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + 1 + i)(currentFigureContainingArrayBuffer(0).horizontalPosition + j))
        }
      }
      val canTheFigureGoDown = !canTheFigureGoDownCheckListBuffer.contains(true)

      if (canTheFigureGoDown) {
        currentFigureContainingArrayBuffer(0) = currentFigureContainingArrayBuffer(0).moveFigureDown()
      }
      else {
        if (currentFigureContainingArrayBuffer(0).verticalPosition <= 0) {
          println(playerName+s"'s SCORE : ${scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0)}")
          val score = scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0)
          scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1)=0
          scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0)=0
          resetGame(score) //GAME is OVER
        }
        else if (presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2).toBoolean) {
          currentFigureContainingArrayBuffer(0) = currentFigureContainingArrayBuffer(0).moveFigureDown()
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2) = "false"
        }
        else {
          formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
        }
      }
      canTheFigureGoDown
    }
  }

  def canMoveTheFigureToLeft: Boolean = {
    val willTheMoveBePossibleCheckListBuffer = ListBuffer[Boolean]()
    for (i <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.indices) {
      for (j <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((currentFigureContainingArrayBuffer(0).horizontalPosition < 1) || (currentFigureContainingArrayBuffer(0).horizontalPosition - 1 + j) >= presetsObject.sceneWidth ||
          (currentFigureContainingArrayBuffer(0).verticalPosition + i) >= presetsObject.sceneHeight ||
          currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + i)(currentFigureContainingArrayBuffer(0).horizontalPosition - 1 + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }

  def canMoveTheFigureToRight: Boolean = {
    val willTheMoveBePossibleCheckListBuffer = ListBuffer[Boolean]()
    for (i <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.indices) {
      for (j <- currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((currentFigureContainingArrayBuffer(0).horizontalPosition + currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(0).length >= presetsObject.sceneWidth) ||
          (currentFigureContainingArrayBuffer(0).horizontalPosition + 1 + j) >= presetsObject.sceneWidth ||
          (currentFigureContainingArrayBuffer(0).verticalPosition + i) >= presetsObject.sceneHeight ||
          currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + i)(currentFigureContainingArrayBuffer(0).horizontalPosition + 1 + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }

  def canRotateTheFigure(isClockWise: Boolean): Boolean = {
    if (isClockWise) {
      figureSupposedToBeRotatedArrayBuffer.addOne(figureSupposedToBeRotatedArrayBuffer(0).rotateFigureClockwise())
    }
    else {
      figureSupposedToBeRotatedArrayBuffer.addOne(figureSupposedToBeRotatedArrayBuffer(0).rotateFigureClockwise())
    }
    val willTheMoveBePossibleCheckListBuffer = scala.collection.mutable.ListBuffer[Boolean]()
    for (i <- figureSupposedToBeRotatedArrayBuffer(0).shapeFormingBooleanMatrix.indices) {
      for (j <- figureSupposedToBeRotatedArrayBuffer(0).shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((currentFigureContainingArrayBuffer(0).horizontalPosition + j) >= presetsObject.sceneWidth ||
          (currentFigureContainingArrayBuffer(0).verticalPosition + i) >= presetsObject.sceneHeight ||
          figureSupposedToBeRotatedArrayBuffer(0).shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(currentFigureContainingArrayBuffer(0).verticalPosition + i)(currentFigureContainingArrayBuffer(0).horizontalPosition + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }
}
