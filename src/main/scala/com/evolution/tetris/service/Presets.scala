package com.evolution.tetris.service

import scala.collection.mutable.ListBuffer
import scala.io.Source

final class Presets {
  val sceneWidth = 16
  val sceneHeight = 24
  val presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType = Array("false", "false", "false", "no bonus")

  def figureCellScale = 30

  def presetFigureShapePatternsSequence: Seq[List[Array[Boolean]]] = {
    val presetFigureShapesList = ListBuffer[List[Array[Boolean]]]()
    val figureShapeDesigningList = ListBuffer[Array[Boolean]]()
    val shapesSource = Source.fromFile("C:/CourseProject/src/main/resources/shapes.txt")
    shapesSource.getLines().foreach(line => {
      if (line.equals("****")) {
        presetFigureShapesList.addOne(figureShapeDesigningList.toList)
        figureShapeDesigningList.clear()
      }
      else {
        val rowFormingShapeDesigningBooleanArray = line.map(charInLine => {
          if (charInLine == '0') false
          else true
        }).toArray
        figureShapeDesigningList.addOne(rowFormingShapeDesigningBooleanArray)
      }
    })
    shapesSource.close()
    presetFigureShapesList.toList
  }
}
