package service

import javafx.scene.shape.Rectangle
import scalafx.application.Platform
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Red
import service.Score.{BONUSSCORE, SCORE, bonusFiguresQuantity}
import tetris.Figure
import tetris.TetrisGame.{fallenFiguresList, figure, fxSceneProtagonists, tetrisSceneBooleanMatrix}

import scala.util.Random

object ServiceFunctions {
  def randomColor(): Color = scalafx.scene.paint.Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))

  def generateRandomOrBonusFigure(): Figure = {
    Presets.bonusType match {
      case "no bonus" =>
        val figureShapeRandomPattern = Presets.presetFigureShapePatternsSequence(math.abs(Random.nextInt(Presets.presetFigureShapePatternsSequence.length)))
        new Figure(Presets.sceneWIDTH / 2, 0, figureShapeRandomPattern.toArray, ServiceFunctions.randomColor())
      //Check the previous 2 lines if smth goes wrong!!!!!!!!!!!!!!!!!!!!
      case "drop on one row down" =>
        bonusFiguresQuantity.set(bonusFiguresQuantity.get() - 1)
        Presets.bonusType = "no bonus"
        Presets.canGetThruTheRow = true
        new Figure(Presets.sceneWIDTH / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.Black)
      case "simple figure" =>
        bonusFiguresQuantity.set(bonusFiguresQuantity.get() - 1)
        if (bonusFiguresQuantity.toInt==0) Presets.bonusType="no bonus"
        new Figure(Presets.sceneWIDTH / 2, 0, Array(Array(x = true)), scalafx.scene.paint.Color.Black)
    }
  }

  def formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore(): Unit = {
    for (i <- figure.shapeFormingBooleanMatrix.indices) {
      for (j <- figure.shapeFormingBooleanMatrix(0).indices) {
        tetrisSceneBooleanMatrix(figure.verticalPosition + i)(figure.horizontalPosition + j) =
          tetrisSceneBooleanMatrix(figure.verticalPosition + i)(figure.horizontalPosition + j) ||
            figure.shapeFormingBooleanMatrix(i)(j)
      }
    }
    fallenFiguresList = fallenFiguresList.appended(figure)
    figure = ServiceFunctions.generateRandomOrBonusFigure()
    SCORE.set(SCORE.get() + 5)
  }

  def showTheFigureOnTheScene(figure: Figure): Unit = {
    try {
      for (i <- figure.shapeFormingBooleanMatrix.indices) {
        for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
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
    } catch {
      case _: ArrayIndexOutOfBoundsException => println("O LA LA!")
      case _: NullPointerException => println("U LO LO!")
    }
  }

  def analyzeTheAvailabilityOfBonusesAddToScoreIfTheRowIsFilledAndReduceTheFilledRow(): Unit = {
    for (i <- 0 until Presets.sceneHEIGHT) {
      var isRowFilled = true
      tetrisSceneBooleanMatrix(i).foreach(tetrisSceneBooleanMatrixRowCell => isRowFilled &&= tetrisSceneBooleanMatrixRowCell)
      if (isRowFilled) {
        BONUSSCORE.set(BONUSSCORE.get() + 1)
        if (BONUSSCORE.toInt % 2 == 0) {
          Presets.bonusType = "simple figure"
          bonusFiguresQuantity.set(BONUSSCORE.toInt / 5 + 1)
        }
        if (BONUSSCORE.toInt % 3 == 0) {
          Presets.figuresChoice = true
        }
        if (BONUSSCORE.toInt % 5 == 0) {
          Presets.bonusType = "drop on one row down"
          bonusFiguresQuantity.set(1)
        }
        SCORE.set(SCORE.get() + 10)
        tetrisSceneBooleanMatrix =
          Array.fill(1, Presets.sceneWIDTH)(false).++(tetrisSceneBooleanMatrix.take(i)).++(tetrisSceneBooleanMatrix.drop(i + 1))
        fallenFiguresList.foreach((figure: Figure) => {
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
      fallenFiguresList.foreach(ServiceFunctions.showTheFigureOnTheScene)
      ServiceFunctions.showTheFigureOnTheScene(figure)
    })
  }

  def makeFigureGoDownQuick(): Unit = {
    while (canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue) {
    }
  }

  def resetGame(): Unit = {
    fallenFiguresList = List()
    tetrisSceneBooleanMatrix = Array.fill[Boolean](Presets.sceneHEIGHT, Presets.sceneWIDTH)(false)
    fxSceneProtagonists.getChildren.clear() //Apparently not needed
    Presets.figuresChoice = false
    Presets.canGetThruTheRow = false
    Presets.bonusType = "no bonus"
  }

  def canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue: Boolean = {
    val indexOfTheRowUnder = figure.verticalPosition + figure.shapeFormingBooleanMatrix.length
    if (indexOfTheRowUnder == Presets.sceneHEIGHT) {
      ServiceFunctions.formResultingHardBottomOfTheSceneAddCurrentFigureToFallenFiguresListCallNextFigureAndAddToScore()
      false
    }
    else {
      var canTheFigureGoDown = true
      for (i <- figure.shapeFormingBooleanMatrix.indices) {
        for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
          if ((figure.horizontalPosition + j) >= Presets.sceneWIDTH ||
            (figure.verticalPosition + 1 + i) >= Presets.sceneHEIGHT ||
            figure.shapeFormingBooleanMatrix(i)(j) &&
              tetrisSceneBooleanMatrix(figure.verticalPosition + 1 + i)(figure.horizontalPosition + j)) {
            canTheFigureGoDown = false
          }
        }
      }
      if (canTheFigureGoDown) figure.moveFigureDown()
      else {
        if (figure.verticalPosition <= 0) {
          println(s"SCORE : ${SCORE.get()}")
          BONUSSCORE.set(0)
          SCORE.set(0)
          ServiceFunctions.resetGame()//GAME is OVER
        }
        else if (Presets.canGetThruTheRow) {
          figure.moveFigureDown()
          Presets.canGetThruTheRow = false
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
    var willTheMoveBePossible = true
    if (figure.horizontalPosition < 1) {
      willTheMoveBePossible = false
    }
    else {
      for (i <- figure.shapeFormingBooleanMatrix.indices) {
        for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
          if ((figure.horizontalPosition - 1 + j) >= Presets.sceneWIDTH ||
            (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
            figure.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrix(figure.verticalPosition + i)(figure.horizontalPosition - 1 + j)) {
            willTheMoveBePossible = false
          }
        }
      }
    }
    willTheMoveBePossible
  }

  def canMoveTheFigureToRight: Boolean = {
    var willTheMoveBePossible = true
    if (figure.horizontalPosition + figure.shapeFormingBooleanMatrix(0).length >= Presets.sceneWIDTH) {
      willTheMoveBePossible = false
    }
    else {
      for (i <- figure.shapeFormingBooleanMatrix.indices) {
        for (j <- figure.shapeFormingBooleanMatrix(i).indices) {
          if ((figure.horizontalPosition + 1 + j) >= Presets.sceneWIDTH ||
            (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
            figure.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrix(figure.verticalPosition + i)(figure.horizontalPosition + 1 + j)) {
            willTheMoveBePossible = false
          }
        }
      }
    }
    willTheMoveBePossible
  }

  def canRotateTheFigure(isClockWise: Boolean): Boolean = {
    val figureSupposedToBeRotated = new Figure(figure.horizontalPosition, figure.verticalPosition, figure.shapeFormingBooleanMatrix.clone(), figure.color)
    if (isClockWise) {
      figureSupposedToBeRotated.rotateFigureClockwise()
    }
    else {
      figureSupposedToBeRotated.rotateFigureAntiClockwise()
    }
    var willRotationBePossible = true
    for (i <- figureSupposedToBeRotated.shapeFormingBooleanMatrix.indices) {
      for (j <- figureSupposedToBeRotated.shapeFormingBooleanMatrix(i).indices) {
        if ((figure.horizontalPosition + j) >= Presets.sceneWIDTH ||
          (figure.verticalPosition + i) >= Presets.sceneHEIGHT ||
          figureSupposedToBeRotated.shapeFormingBooleanMatrix(i)(j) && tetrisSceneBooleanMatrix(figure.verticalPosition + i)(figure.horizontalPosition + j)) {
          willRotationBePossible = false
        }
      }
    }
    willRotationBePossible
  }
}
