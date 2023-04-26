package com.evolution.tetris.game

import com.evolution.tetris.service.Presets

final case class Figure(val horizontalPosition:Int, val verticalPosition:Int = 0,
             val shapeFormingBooleanMatrix: Array[Array[Boolean]], val color:scalafx.scene.paint.Color,
             presetsObject: Presets) {

  def rotateFigureClockwise(): Figure =
    new Figure(horizontalPosition, verticalPosition, shapeFormingBooleanMatrix.reverse.transpose[Boolean], color,
      presetsObject)

  def rotateFigureAntiClockwise(): Figure = {
    rotateFigureClockwise()
    rotateFigureClockwise()
    rotateFigureClockwise()
  }

  def moveFigureToRight(): Figure =
    new Figure(horizontalPosition+1,verticalPosition,shapeFormingBooleanMatrix,color,presetsObject)

  def moveFigureToLeft(): Figure =
    new Figure(horizontalPosition-1,verticalPosition,shapeFormingBooleanMatrix,color,presetsObject)

  def moveFigureDown(): Figure =
    if (!presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean)
      new Figure(horizontalPosition,verticalPosition+1,shapeFormingBooleanMatrix,color,presetsObject)
    else this
}
