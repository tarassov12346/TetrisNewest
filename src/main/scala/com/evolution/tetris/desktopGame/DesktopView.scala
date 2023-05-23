package com.evolution.tetris.desktopGame

import com.evolution.tetris.db.PlayerDao
import com.evolution.tetris.service.{Figure, Presets, ServiceFunctions}
import javafx.scene.shape.Rectangle
import scalafx.application.Platform
import scalafx.beans.property.IntegerProperty
import scalafx.scene.Group
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Blue, DarkGoldenrod, Red}
import scalafx.scene.text.{Font, Text}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

final case class DesktopView(playerName: String, playerDao: PlayerDao) {

  val serviceFunctions = new ServiceFunctions(playerName, playerDao)
  val presetsObject = new Presets()
  val fxSceneProtagonists = new Group()

  val scoreText: Text = new Text() {
    this.text = "SCORE: __"
    this.layoutX = 2
    this.layoutY = 15
    this.stroke = Blue
    this.font.value = new Font("Comic-sans", 13)
  }

  val score: IntegerProperty = new IntegerProperty() {
    onChange { (_, _, newValue) =>
      Platform.runLater(() -> {
        scoreText.setText(serviceFunctions.playerName + "'s " + s"SCORE: ${newValue.toString} \n " +
          s"Assigned bonus simple figures: ${bonusFiguresQuantity.toInt}\n" +
          s" BONUS SCORE: ${bonusScore.toInt}\n" +
          s" Can use C-key to change a figure: ${serviceFunctions.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1)}\n" +
          s" Can get thru the row: ${serviceFunctions.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2)}")
      })
    }
  }
  val bonusScore: IntegerProperty = new IntegerProperty()
  val bonusFiguresQuantity: IntegerProperty = new IntegerProperty()

  def randomColor(): Color = scalafx.scene.paint.Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))

  val randomColorArray: Array[Color] = Array.fill(10) {
    randomColor()
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
            figure.colorChoiceCode match {
              case 12 => rectangle.setFill(DarkGoldenrod)
              case 13 => rectangle.setFill(Black)
              case _ => rectangle.setFill(randomColorArray(figure.colorChoiceCode))
            }
            rectangle.setStroke(Red)
            rectangle.setArcHeight(2.4)
            sfxGroup2jfx(fxSceneProtagonists).getChildren.add(rectangle)
          }
        }
      }
    }
    score.set(serviceFunctions.scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(0))
    bonusScore.set(serviceFunctions.scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(1))
    bonusFiguresQuantity.set(serviceFunctions.scoreArrayOfScoreAndBonusScoreAndBonusFigureQuantity(2))
  }

  def showFallenFiguresAndCurrentFigure(fallenFiguresListBuffer: ListBuffer[Figure], currentFigureContainingArrayBuffer: ArrayBuffer[Figure]): Unit = {
    Platform.runLater(() -> {
      fxSceneProtagonists.getChildren.clear() //to clean up the traces from falling figures
      fallenFiguresListBuffer.foreach(showTheFigureOnTheScene)
      showTheFigureOnTheScene(currentFigureContainingArrayBuffer(0))
    })
  }
}
