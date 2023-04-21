package com.evolution.tetris.game

import scalafx.application.JFXApp3
import scalafx.concurrent.{ScheduledService, Task}
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}
import com.evolution.tetris.service.Score.{SCORE, score}
import com.evolution.tetris.service.{Presets, ServiceFunctions}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object TetrisGame extends JFXApp3 {

  val fallenFiguresListBuffer = ListBuffer[Figure]()
  val tetrisSceneBooleanMatrixArrayBuffer: ArrayBuffer[ArrayBuffer[Boolean]] = ArrayBuffer.fill[Boolean](Presets.sceneHEIGHT, Presets.sceneWIDTH)(false)
  val fxSceneProtagonists = new Group()

  SCORE.set(0)
  var figure: Figure = ServiceFunctions.generateRandomOrBonusFigure()

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "TETRIS DESC-TOP GAME"
      val scheduledTask: ScheduledService[(Unit, Unit)] = ScheduledService.apply(
        Task.apply(() -> {
          ServiceFunctions.canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue
          ServiceFunctions.analyzeTheAvailabilityOfBonusesAddToScoreIfTheRowIsFilledAndReduceTheFilledRow()
        })
      )
      scheduledTask.setPeriod(javafx.util.Duration.seconds(1))
      scene = new Scene(Presets.sceneWIDTH * Presets.figureCellScale, Presets.sceneHEIGHT * Presets.figureCellScale) {
        fill = Color.rgb(192, 192, 192)
        val Root = new Group()
        sfxGroup2jfx(Root).getChildren.addAll(fxSceneProtagonists, score)
        root = Root
        onKeyPressed = key => {
          key.getCode match {
            case KeyCode.Left.delegate => if (ServiceFunctions.canMoveTheFigureToLeft) figure.moveFigureToLeft()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.Right.delegate => if (ServiceFunctions.canMoveTheFigureToRight) figure.moveFigureToRight()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.Space.delegate => if (ServiceFunctions.canRotateTheFigure(true)) figure.rotateFigureClockwise()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.Alt.delegate => if (ServiceFunctions.canRotateTheFigure(false)) figure.rotateFigureAntiClockwise()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.Down.delegate => if (!Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(0).toBoolean) ServiceFunctions.makeFigureGoDownQuick()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.P.delegate => Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(0) = (!Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(0).toBoolean).toString
              //Presets.pause = !Presets.pause
            case KeyCode.C.delegate => if (Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(1).toBoolean) figure = ServiceFunctions.generateRandomOrBonusFigure()
              Presets.presetsArrayOfPauseFiguresChoiceBreakThruAbilityBonusType(1) = "false"
            case _ => println("Use Left,Right,Space,Alt,Down keys!")
          }
        }
        scheduledTask.start()
      }
    }
    stage.setResizable(false)
  }
}
