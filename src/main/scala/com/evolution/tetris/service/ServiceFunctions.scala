package com.evolution.tetris.service

import javafx.scene.shape.Rectangle
import scalafx.application.Platform
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Red
import Score.{BONUSSCORE, SCORE, bonusFiguresQuantity}
import com.evolution.tetris.game.Figure
import com.evolution.tetris.game.TetrisGame.{fallenFiguresListBuffer, figure, fxSceneProtagonists, tetrisSceneBooleanMatrixArrayBuffer}
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object ServiceFunctions {
  def randomColor(): Color = scalafx.scene.paint.Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))

  def generateRandomOrBonusFigure(): Figure = {
    Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) match {
      case "no bonus" =>
        val figureShapeRandomPattern = Presets.presetFigureShapePatternsSequence(math.abs(Random.nextInt(Presets.presetFigureShapePatternsSequence.length)))
        new Figure(Presets.sceneWIDTH / 2, 0, figureShapeRandomPattern.toArray, ServiceFunctions.randomColor())
      //Check the previous 2 lines if smth goes wrong!!!!!!!!!!!!!!!!!!!!
      case "drop on one row down" =>
        bonusFiguresQuantity.set(bonusFiguresQuantity.get() - 1)
        Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) = "no bonus"
        Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(2) = "true"
        new Figure(Presets.sceneWIDTH / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.Black)
      case "simple figure" =>
        bonusFiguresQuantity.set(bonusFiguresQuantity.get() - 1)
        if (bonusFiguresQuantity.toInt == 0) Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) = "no bonus"
        new Figure(Presets.sceneWIDTH / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.Black)
    }
  }

  def formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore(): Unit = {
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(0).indices) {
        tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + i)(figure.horizontalPosition + j) =
          tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + i)(figure.horizontalPosition + j) ||
            figure.shapeFormingBooleanMatrix(i)(j)
      }
    }
    fallenFiguresListBuffer.addOne(figure)
    figure = ServiceFunctions.generateRandomOrBonusFigure()
    SCORE.set(SCORE.get() + 5)
  }

  def showTheFigureOnTheScene(figure: Figure): Unit = {
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
        if (figure.shapeFormingBooleanMatrix(i).nonEmpty) {
          if (figure.shapeFormingBooleanMatrix(i)(j)) {
            val rectangle = new Rectangle()
            rectangle.setX((figure.horizontalPosition + j) * Presets.figureCellScale)
            rectangle.setY((figure.verticalPosition + i) * Presets.figureCellScale)
            rectangle.setWidth(Presets.figureCellScale)
            rectangle.setHeight(Presets.figureCellScale)
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
    for (i <- 0 until Presets.sceneHEIGHT) {
      val isRowFilled = !tetrisSceneBooleanMatrixArrayBuffer(i).contains(false)
      if (isRowFilled) {
        BONUSSCORE.set(BONUSSCORE.get() + 1)
        if (BONUSSCORE.toInt % 2 == 0) {
          Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) = "simple figure"
          bonusFiguresQuantity.set(BONUSSCORE.toInt / 5 + 1)
        }
        if (BONUSSCORE.toInt % 3 == 0) {
          Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(1) = "true"
        }
        if (BONUSSCORE.toInt % 5 == 0) {
          Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) = "drop on one row down"
          bonusFiguresQuantity.set(1)
        }
        SCORE.set(SCORE.get() + 10)
        tetrisSceneBooleanMatrixArrayBuffer.remove(i)
        tetrisSceneBooleanMatrixArrayBuffer.prepend(ArrayBuffer.fill(Presets.sceneWIDTH)(false))
        fallenFiguresListBuffer.foreach((figure: Figure) => {
          if (figure.verticalPosition <= i && figure.verticalPosition + figure.shapeFormingBooleanMatrix.length >= i) {
            figure.shapeFormingBooleanMatrix = figure.shapeFormingBooleanMatrix.take(i - figure.verticalPosition).++(figure.shapeFormingBooleanMatrix.drop(i - figure.verticalPosition + 1))
          }
          if (figure.verticalPosition <= i) {
            figure.verticalPosition += 1
          }
        })
        showFallenFiguresAndCurrentFigure()
      }
    }
  }


  def showFallenFiguresAndCurrentFigure(): Unit = {
    Platform.runLater(() -> {
      fxSceneProtagonists.getChildren.clear() //to clean up the traces from falling figures
      fallenFiguresListBuffer.foreach(ServiceFunctions.showTheFigureOnTheScene)
      ServiceFunctions.showTheFigureOnTheScene(figure)
    })
  }

  def makeFigureGoDownQuick(): Unit = {
    while (canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue) {
    }
  }

  def resetGame(): Unit = {
    fallenFiguresListBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.clear()
    tetrisSceneBooleanMatrixArrayBuffer.addAll(ArrayBuffer.fill[Boolean](Presets.sceneHEIGHT, Presets.sceneWIDTH)(false))
    fxSceneProtagonists.getChildren.clear() //Apparently not needed
    Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(1) = "false"
    Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(2) = "false"
    Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(3) = "no bonus"
  }

  def canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue: Boolean = {
    val indexOfTheRowUnder = figure.verticalPosition + figure.shapeFormingBooleanMatrix.length
    if (indexOfTheRowUnder == Presets.sceneHEIGHT) {
      ServiceFunctions.formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
      false
    }
    else {
      val canTheFigureGoDownCheckListBuffer = scala.collection.mutable.ListBuffer[Boolean]()
      for (i <- figure.shapeFormingBooleanMatrix.indices) {
        for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
          canTheFigureGoDownCheckListBuffer.addOne((figure.horizontalPosition + j) >= Presets.sceneWIDTH ||
            (figure.verticalPosition + 1 + i) >= Presets.sceneHEIGHT ||
            figure.shapeFormingBooleanMatrix(i)(j) &&
              tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + 1 + i)(figure.horizontalPosition + j))
        }
      }
      val canTheFigureGoDown = !canTheFigureGoDownCheckListBuffer.contains(true)
      if (canTheFigureGoDown) figure.moveFigureDown()
      else {
        if (figure.verticalPosition <= 0) {
          println(s"SCORE : ${SCORE.get()}")
          BONUSSCORE.set(0)
          SCORE.set(0)
          ServiceFunctions.resetGame() //GAME is OVER
        }
        else if (Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(2).toBoolean) {
          figure.moveFigureDown()
          Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(2) = "false"
        }
        else {
          ServiceFunctions.formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
        }
      }
      ServiceFunctions.showFallenFiguresAndCurrentFigure()
      canTheFigureGoDown
    }
  }

  def canMoveTheFigureToLeft: Boolean = {
    val willTheMoveBePossibleCheckListBuffer = scala.collection.mutable.ListBuffer[Boolean]()
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((figure.horizontalPosition < 1) || (figure.horizontalPosition - 1 + j) >= Presets.sceneWIDTH ||
          (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
          figure.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + i)(figure.horizontalPosition - 1 + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }

  def canMoveTheFigureToRight: Boolean = {
    val willTheMoveBePossibleCheckListBuffer = scala.collection.mutable.ListBuffer[Boolean]()
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((figure.horizontalPosition + figure.shapeFormingBooleanMatrix(0).length >= Presets.sceneWIDTH) ||
          (figure.horizontalPosition + 1 + j) >= Presets.sceneWIDTH ||
          (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
          figure.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + i)(figure.horizontalPosition + 1 + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }

  def canRotateTheFigure(isClockWise: Boolean): Boolean = {
    val figureSupposedToBeRotated = new Figure(figure.horizontalPosition, figure.verticalPosition, figure.shapeFormingBooleanMatrix.clone(), figure.color)
    if (isClockWise) {
      figureSupposedToBeRotated.rotateFigureClockwise()
    }
    else {
      figureSupposedToBeRotated.rotateFigureAntiClockwise()
    }
    val willTheMoveBePossibleCheckListBuffer = scala.collection.mutable.ListBuffer[Boolean]()
    for (i <- figureSupposedToBeRotated.shapeFormingBooleanMatrix.indices) {
      for (j <- figureSupposedToBeRotated.shapeFormingBooleanMatrix(i).indices) {
        willTheMoveBePossibleCheckListBuffer.addOne((figure.horizontalPosition + j) >= Presets.sceneWIDTH ||
          (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
          figureSupposedToBeRotated.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrixArrayBuffer(figure.verticalPosition + i)(figure.horizontalPosition + j))
      }
    }
    !willTheMoveBePossibleCheckListBuffer.contains(true)
  }
}
