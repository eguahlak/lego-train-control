package info.itseminar.lego

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.TrainConfig
import info.itseminar.lego.protocol.trainManager
import kotlinx.android.synthetic.main.activity_socket_experiment.*
import kotlinx.android.synthetic.main.activity_traingui.*
import java.io.IOException
import java.net.Socket

class TrainGui : AppCompatActivity() {

  var speed = 0


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_traingui)

    speedUpButton.setOnClickListener {
      val increase = 5
      speed += increase
      trainManager().send(Command.TrainControl(speed.toString().toIntOrNull() ?: 0))
    }
    speedDownButton.setOnClickListener {
      val decrease = 5
      speed -= decrease
      trainManager().send(Command.TrainControl(speed.toString().toIntOrNull() ?: 0))
    }
    listenAgainButton.setOnClickListener {
      trainManager().setOnInformation { command ->
        when (command) {
          is Command.TrainInformation -> {
            currentSpeedLabel.setText("${command.speed} km/h")
            speed = command.speed
          }
          else -> {
            Log.w("TRAIN", "Unknown command: $command")
          }
        }
      }
    }
    stopButton.setOnClickListener {
      for (x in 0 until speed step 5) {
        val stopspeed = 5
        speed -= stopspeed
        Thread.sleep(500)
        trainManager().send(Command.TrainControl(speed.toString().toIntOrNull() ?: 0))
      }
    }
  }
}