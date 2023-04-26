package com.evolution.tetris.service

import scalafx.application.Platform
import scalafx.beans.property.IntegerProperty
import scalafx.scene.paint.Color.Blue
import scalafx.scene.text.{Font, Text}

final case class Score (presetsObject : Presets) {

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
        scoreText.setText(s"SCORE: ${newValue.toString} \n " +
          s"Assigned bonus simple figures: ${bonusFiguresQuantity.toInt}\n" +
          s" BONUS SCORE: ${bonusScore.toInt}\n" +
          s" Can use C-key to change a figure: ${presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1)}\n"+
          s" Can get thru the row: ${presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(2)}")

      })
    }
  }
  val bonusScore: IntegerProperty = new IntegerProperty()
  val bonusFiguresQuantity: IntegerProperty = new IntegerProperty()
}
