package com.evolution.tetris.service

import scalafx.application.Platform
import scalafx.beans.property.IntegerProperty
import scalafx.scene.paint.Color.Blue
import scalafx.scene.text.{Font, Text}

object Score {
  val score: Text = new Text() {
    this.text = "SCORE: __"
    this.layoutX = 2
    this.layoutY = 15
    this.stroke = Blue
    this.font.value = new Font("Comic-sans", 13)
  }
  val SCORE: IntegerProperty = new IntegerProperty() {
    onChange { (_, _, newValue) =>
      Platform.runLater(() -> {
        score.setText(s"SCORE: ${newValue.toString} \n " +
          s"Assigned bonus simple figures: ${bonusFiguresQuantity.toInt}\n" +
          s" BONUSSCORE: ${BONUSSCORE.toInt}\n" +
          s" Can use C-key to change a figure: ${Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(1)}\n"+
          s" Can get thru the row: ${Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(2)}")

      })
    }
  }
  val BONUSSCORE: IntegerProperty = new IntegerProperty()
  val bonusFiguresQuantity: IntegerProperty = new IntegerProperty()
}
