package com.evolution.tetris.game

import com.evolution.tetris.service.ServiceFunctions
import scalafx.application.JFXApp3
import scalafx.concurrent.{ScheduledService, Task}
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}

final case class TetrisGame(playerName: String) extends JFXApp3 {

  val service = new ServiceFunctions(playerName)


  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "TETRIS DESC-TOP GAME"
      val scheduledTask: ScheduledService[(Unit, Unit)] = ScheduledService.apply(
        Task.apply(() -> {
         service.canCurrentFigureGoDownCheckAndMoveTheFigureAtOnePositionDownIfTrue
          service.analyzeTheAvailabilityOfBonusesAddToScoreIfTheRowIsFilledAndReduceTheFilledRow()
          service.showFallenFiguresAndCurrentFigure()
        })
      )
      scheduledTask.setPeriod(javafx.util.Duration.seconds(1))
      scene = new Scene(service.presetsObject.sceneWidth * service.presetsObject.figureCellScale, service.presetsObject.sceneHeight * service.presetsObject.figureCellScale) {
        fill = Color.rgb(192, 192, 192)
        val Root = new Group()
        sfxGroup2jfx(Root).getChildren.addAll(service.fxSceneProtagonists, service.scoreObject.scoreText)
        root = Root
        onKeyPressed = key => {
          key.getCode match {
            case KeyCode.Left.delegate => if (service.canMoveTheFigureToLeft) service.currentFigureContainingArrayBuffer(0) = service.currentFigureContainingArrayBuffer(0).moveFigureToLeft()
              service.showFallenFiguresAndCurrentFigure()
            case KeyCode.Right.delegate => if (service.canMoveTheFigureToRight) service.currentFigureContainingArrayBuffer(0) = service.currentFigureContainingArrayBuffer(0).moveFigureToRight()
              service.showFallenFiguresAndCurrentFigure()
            case KeyCode.Space.delegate => if (service.canRotateTheFigure(true)) service.currentFigureContainingArrayBuffer(0) = service.currentFigureContainingArrayBuffer(0).rotateFigureClockwise()
              service.showFallenFiguresAndCurrentFigure()
            case KeyCode.Alt.delegate => if (service.canRotateTheFigure(false)) service.currentFigureContainingArrayBuffer(0) = service.currentFigureContainingArrayBuffer(0).rotateFigureAntiClockwise()
              service.showFallenFiguresAndCurrentFigure()
            case KeyCode.Down.delegate => if (!service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean) service.makeFigureGoDownQuick()
             service.showFallenFiguresAndCurrentFigure()
            case KeyCode.P.delegate => service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0) = (!service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(0).toBoolean).toString
            case KeyCode.C.delegate => if (service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1).toBoolean) service.currentFigureContainingArrayBuffer(0) = service.generateRandomOrBonusFigure()
              service.presetsObject.presetsArrayOfPauseAndFiguresChoiceAndBreakThruAbilityAndBonusType(1) = "false"
            case _ => println("Use Left,Right,Space,Alt,Down keys!")
          }
        }
        scheduledTask.start()
      }
    }
    stage.setResizable(false)
  }
}
