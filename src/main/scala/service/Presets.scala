package service

import scala.io.Source

object Presets {
  var sceneWIDTH = 16
  var sceneHEIGHT = 12
  var pause = false
  var figuresChoice = false
  var canGetThruTheRow = false
  var bonusType = "no bonus"

  def figureCellScale = 30

  def presetFigureShapePatternsSequence: Seq[List[Array[Boolean]]] = {
    var presetFigureShapesList = List[List[Array[Boolean]]]()
    var figureShapeDesigningList = List[Array[Boolean]]()
    val shapesSource=Source.fromFile("shapes.txt")
    shapesSource.getLines().foreach(line => {
      if (line.equals("****")) {
        presetFigureShapesList = presetFigureShapesList.appended(figureShapeDesigningList)
        figureShapeDesigningList = List()
      }
      else {
        val rowFormingShapeDesigningBooleanArray = new Array[Boolean](line.length)
        var i = 0
        line.foreach(charInLine => {
          if (charInLine == '0') {
            rowFormingShapeDesigningBooleanArray(i) = false
          }
          else {
            rowFormingShapeDesigningBooleanArray(i) = true
          }
          i += 1
        })
        figureShapeDesigningList = figureShapeDesigningList.appended(rowFormingShapeDesigningBooleanArray)
      }
    })
    shapesSource.close()
    presetFigureShapesList
  }
}
