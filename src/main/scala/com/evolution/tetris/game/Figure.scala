package com.evolution.tetris.game

import com.evolution.tetris.service.Presets

class Figure(var horizontalPosition:Int, var verticalPosition:Int = 0,
             var shapeFormingBooleanMatrix: Array[Array[Boolean]], val color:scalafx.scene.paint.Color) {

  def rotateFigureClockwise(): (Unit, Unit) =()->{
    this.shapeFormingBooleanMatrix = shapeFormingBooleanMatrix.reverse.transpose[Boolean]
  }

  def rotateFigureAntiClockwise(): (Unit, (Unit, Unit)) =()->{
    this.rotateFigureClockwise()
    this.rotateFigureClockwise()
    this.rotateFigureClockwise()
  }

  def moveFigureToRight(): Unit =  horizontalPosition+=1

  def moveFigureToLeft(): Unit =  horizontalPosition-=1

  def moveFigureDown(): Unit = if (!Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(0).toBoolean) verticalPosition+=1
}
