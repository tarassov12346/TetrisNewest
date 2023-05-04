package com.evolution.tetris.service

final case class Figure(horizontalPosition: Int, verticalPosition: Int = 0,
                        shapeFormingBooleanMatrix: Array[Array[Boolean]], color: scalafx.scene.paint.Color,
                        presetsObject: Presets) {

  def rotateFigureClockwise(): Figure =
    Figure(horizontalPosition, verticalPosition, shapeFormingBooleanMatrix.reverse.transpose[Boolean], color,
      presetsObject)

  def rotateFigureAntiClockwise(): Figure = {
    rotateFigureClockwise()
    rotateFigureClockwise()
    rotateFigureClockwise()
  }

  def moveFigureToRight(): Figure =
    Figure(horizontalPosition + 1, verticalPosition, shapeFormingBooleanMatrix, color, presetsObject)

  def moveFigureToLeft(): Figure =
    Figure(horizontalPosition - 1, verticalPosition, shapeFormingBooleanMatrix, color, presetsObject)

  def moveFigureDown(): Figure =
    if (!presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean)
      Figure(horizontalPosition, verticalPosition + 1, shapeFormingBooleanMatrix, color, presetsObject)
    else this
}
