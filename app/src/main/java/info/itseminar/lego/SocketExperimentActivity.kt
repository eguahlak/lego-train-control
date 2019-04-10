package info.itseminar.lego

import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.TrainConfig
import info.itseminar.lego.protocol.trainManager
import kotlinx.android.synthetic.main.activity_socket_experiment.*

class SocketExperimentActivity : AppCompatActivity() {
  var informationCount = 0


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_socket_experiment)
    val host = intent.extras.getString("host").trim()

    trainManager().connect(TrainConfig(host, 4711, 17)) {
      if (it) {
        speedButton.setText("Set Target Speed")
        }
      else {
        speedButton.setText("Connection failed")
        }
      }
    listenButton.setOnClickListener {
      trainManager().setOnInformation { command ->
        when (command) {
          is Command.TrainInformation -> {
            speed_label.setText("${command.speed} km/h")
            }
          else -> {
            Log.w("TRAIN", "Unknown command: $command")
            }
          }
        }
      }
    speedButton.setOnClickListener {
      trainManager().send(Command.TrainControl(targetSpeedText.text.toString().toIntOrNull() ?: 0))
      }
    }

  }
