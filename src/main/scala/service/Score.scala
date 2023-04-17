package service

import scalafx.application.Platform
import scalafx.beans.property.IntegerProperty
import scalafx.scene.paint.Color.Blue
import scalafx.scene.text.{Font, Text}

object Score {
  var score: Text = new Text() {
    this.text = "SCORE: __"
    this.layoutX = 2
    this.layoutY = 15
    this.stroke = Blue
    this.font.value = new Font("Comic-sans", 13)
  }
  var SCORE: IntegerProperty = new IntegerProperty() {
    onChange { (_, _, newValue) =>
      Platform.runLater(() -> {
        score.setText(s"SCORE: ${newValue.toString} \n " +
          s"Assigned bonus simple figures: ${bonusFiguresQuantity.toInt}\n" +
          s" BONUSSCORE: ${BONUSSCORE.toInt}\n" +
          s" Can use C-key to change a figure: ${Presets.figuresChoice}\n"+
          s" Can get thru the row: ${Presets.canGetThruTheRow}")

      })
    }
  }
  var BONUSSCORE: IntegerProperty = new IntegerProperty()
  var bonusFiguresQuantity: IntegerProperty = new IntegerProperty()
}
