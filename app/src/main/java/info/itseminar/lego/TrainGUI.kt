package info.itseminar.lego

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.trainManager
import kotlinx.android.synthetic.main.activity_traingui.*
import com.github.anastr.speedviewlib.SpeedView
import kotlinx.android.synthetic.main.activity_traingui.view.*

class TrainGui : AppCompatActivity() {

  var speed = 0
  var targetspeed = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_traingui)
    var speedometer: SpeedView = speedView
    speedometer.setWithTremble(false)
    traffic.


    speedUpButton.setOnClickListener {
      val increase = 5
      targetspeed += increase
      trainManager().send(Command.TrainControl(targetspeed.toString().toIntOrNull() ?: 0))
      currentSpeedLabel.setText("${targetspeed} km/h")
    }
    speedDownButton.setOnClickListener {
      val decrease = 5
      targetspeed -= decrease
      trainManager().send(Command.TrainControl(targetspeed.toString().toIntOrNull() ?: 0))
      currentSpeedLabel.setText("${targetspeed} km/h")
    }
    listenAgainButton.setOnClickListener {
      trainManager().setOnInformation { command ->
        when (command) {
          is Command.TrainInformation -> {
            speed = command.speed
            speedometer.speedTo(speed.toFloat(), 1000)
          }
          else -> {
            Log.w("TRAIN", "Unknown command: $command")
          }
        }
      }
    }

    stopButton.setOnClickListener {
      for (x in 0 until targetspeed step 5) {
        val stopspeed = 5
        targetspeed -= stopspeed
        Thread.sleep(100)
        trainManager().send(Command.TrainControl(targetspeed.toString().toIntOrNull() ?: 0))
        currentSpeedLabel.setText("${targetspeed} km/h")
      }
    }
  }
}