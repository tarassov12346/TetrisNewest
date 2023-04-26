package com.evolution.tetris.service

import com.evolution.tetris.game.Figure
import javafx.scene.shape.Rectangle
import scalafx.application.Platform
import scalafx.scene.Group
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Red

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

final class ServiceFunctions {


  val presetsObject = new Presets()
  val scoreObject = new Score(presetsObject)

  val tetrisSceneBooleanMatrixArrayBuffer: ArrayBuffer[ArrayBuffer[Boolean]] = ArrayBuffer.fill[Boolean](presetsObject.sceneHeight, presetsObject.sceneWidth)(false)
  val fallenFiguresListBuffer = ListBuffer[Figure]()
  val fxSceneProtagonists = new Group()
  val currentFigureContainingArrayBuffer: ArrayBuffer[Figure] = ArrayBuffer.fill[Figure](1)(generateRandomOrBonusFigure())
  val figureSupposedToBeRotatedArrayBuffer = ArrayBuffer[Figure](new Figure(currentFigureContainingArrayBuffer(0).horizontalPosition, currentFigureContainingArrayBuffer(0).verticalPosition, currentFigureContainingArrayBuffer(0).shapeFormingBooleanMatrix.clone(), currentFigureContainingArrayBuffer(0).color,presetsObject))


  def randomColor(): Color = scalafx.scene.paint.Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))

  def generateRandomOrBonusFigure(): Figure = {
    presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) match {
      case "no bonus" =>
        val figureShapeRandomPattern = presetsObject.presetFigureShapePatternsSequence(math.abs(Random.nextInt(presetsObject.presetFigureShapePatternsSequence.length)))
        new Figure(presetsObject.sceneWidth / 2, 0, figureShapeRandomPattern.toArray, randomColor(),presetsObject)
      //Check the previous 2 lines if smth goes wrong!!!!!!!!!!!!!!!!!!!!
      case "drop on one row down" =>
        scoreObject.bonusFiguresQuantity.set(scoreObject.bonusFiguresQuantity.get() - 1)
        presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "no bonus"
        presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2) = "true"
        new Figure(presetsObject.sceneWidth / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.DarkGoldenrod,presetsObject)
      case "simple figure" =>
        scoreObject.bonusFiguresQuantity.set(scoreObject.bonusFiguresQuantity.get() - 1)
        if (scoreObject.bonusFiguresQuantity.toInt == 0) presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "no bonus"
        new Figure(presetsObject.sceneWidth / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.Black,presetsObject)
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
    scoreObject.score.set(scoreObject.score.get() + 5)
  }

  def showTheFigureOnTheScene(figure: Figure): Unit = {
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
        if (figure.shapeFormingBooleanMatrix(i).nonEmpty) {
          if (figure.shapeFormingBooleanMatrix(i)(j)) {
            val rectangle = new Rectangle()
            rectangle.setX((figure.horizontalPosition + j) * presetsObject.figureCellScale)
            rectangle.setY((figure.verticalPosition + i) * presetsObject.figureCellScale)
            rectangle.setWidth(presetsObject.figureCellScale)
            rectangle.setHeight(presetsObject.figureCellScale)
            rectangle.setFill(figure.color)
            rectangle.setStroke(Red)
            rectangle.setArcHeight(2.4)
            sfxGroup2jfx(fxSceneProtagonists).getChildren.add(rectangle)
          }
        }
      }
    }
  }

  def analyzeTheAvailabilityOfBonusesAddToScoreIfTheRowIsFilledAndReduceTheFilledRow(): Unit = {
    for (i <- 0 until presetsObject.sceneHeight) {
      val isRowFilled = !tetrisSceneBooleanMatrixArrayBuffer(i).contains(false)
      if (isRowFilled) {
        scoreObject.bonusScore.set(scoreObject.bonusScore.get() + 1)
        if (scoreObject.bonusScore.toInt % 2 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "simple figure"
          scoreObject.bonusFiguresQuantity.set(scoreObject.bonusScore.toInt / 5 + 1)
        }
        if (scoreObject.bonusScore.toInt % 3 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1) = "true"
        }
        if (scoreObject.bonusScore.toInt % 5 == 0) {
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(3) = "drop on one row down"
          scoreObject.bonusFiguresQuantity.set(1)
        }
        scoreObject.score.set(scoreObject.score.get() + 10)
        tetrisSceneBooleanMatrixArrayBuffer.remove(i)
        tetrisSceneBooleanMatrixArrayBuffer.prepend(ArrayBuffer.fill(presetsObject.sceneWidth)(false))

        fallenFiguresListBuffer.mapInPlace(fallenFigure => {
          if (fallenFigure.verticalPosition <= i && fallenFigure.verticalPosition + fallenFigure.shapeFormingBooleanMatrix.length >= i)
            new Figure(fallenFigure.horizontalPosition,fallenFigure.verticalPosition,
              fallenFigure.shapeFormingBooleanMatrix.take(i - fallenFigure.verticalPosition).++(fallenFigure.shapeFormingBooleanMatrix.drop(i - fallenFigure.verticalPosition + 1)),
              fallenFigure.color,presetsObject)
              else fallenFigure
        })

        fallenFiguresListBuffer.mapInPlace(fallenFigure => {
          if (fallenFigure.verticalPosition <=i) {
            new Figure(fallenFigure.horizontalPosition,fallenFigure.verticalPosition+1,
              fallenFigure.shapeFormingBooleanMatrix,fallenFigure.color,presetsObject)
          } else fallenFigure
        })

        //showFallenFiguresAndCurrentFigure()
      }
    }
  }


  def showFallenFiguresAndCurrentFigure(): Unit = {
    Platform.runLater(() -> {
      fxSceneProtagonists.getChildren.clear() //to clean up the traces from falling figures
      fallenFiguresListBuffer.foreach(showTheFigureOnTheScene)
      showTheFigureOnTheScene(currentFigureContainingArrayBuffer(0))
    })
  }

  def makeFigureGoDownQuick(): Unit = {
    while (canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue) {
    }
  }

  def resetGame(): Unit = {
    fallenFiguresListBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.addAll(ArrayBuffer.fill[Boolean](presetsObject.sceneHeight, presetsObject.sceneWidth)(false))
    fxSceneProtagonists.getChildren.clear() //Apparently not needed
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
        currentFigureContainingArrayBuffer(0)= currentFigureContainingArrayBuffer(0).moveFigureDown()
      }
      else {
        if (currentFigureContainingArrayBuffer(0).verticalPosition <= 0) {
          println(s"SCORE : ${scoreObject.score.get()}")
          scoreObject.bonusScore.set(0)
          scoreObject.score.set(0)
          resetGame() //GAME is OVER
        }
        else if (presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2).toBoolean) {
          currentFigureContainingArrayBuffer(0) = currentFigureContainingArrayBuffer(0).moveFigureDown()
          presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2) = "false"
        }
        else {
          formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
        }
      }
      //ServiceFunctions.showFallenFiguresAndCurrentFigure()
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
