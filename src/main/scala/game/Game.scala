package game

import scalafx.application.JFXApp3

trait Game extends JFXApp3{
  def Launch(): Unit = {
    this.main(Array())
  }
}
