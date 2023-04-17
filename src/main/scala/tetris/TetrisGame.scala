package tetris

import game.Game
import scalafx.application.JFXApp3
import scalafx.concurrent.{ScheduledService, Task}
import scalafx.scene.Group.sfxGroup2jfx
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}
import service.{Presets, ServiceFunctions}
import service.Score.{SCORE, score}

object TetrisGame extends Game {

  var fallenFiguresList: List[Figure] = List()
  var tetrisSceneBooleanMatrix: Array[Array[Boolean]] = Array.fill[Boolean](Presets.sceneHEIGHT, Presets.sceneWIDTH)(false)
  var fxSceneProtagonists = new Group()

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
            case KeyCode.Down.delegate => if (!Presets.pause) ServiceFunctions.makeFigureGoDownQuick()
              ServiceFunctions.showFallenFiguresAndCurrentFigure()
            case KeyCode.P.delegate => Presets.pause = !Presets.pause
            case KeyCode.C.delegate => if (Presets.figuresChoice) figure = ServiceFunctions.generateRandomOrBonusFigure()
              Presets.figuresChoice = false
            case _ => println("Use Left,Right,Space,Alt,Down keys!")
          }
        }
        scheduledTask.start()
      }
    }
    stage.setResizable(false)
  }
}
