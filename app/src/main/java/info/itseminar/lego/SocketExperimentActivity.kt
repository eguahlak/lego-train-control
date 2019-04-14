package info.itseminar.lego

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.*
import kotlinx.android.synthetic.main.activity_socket_experiment.*

class SocketExperimentActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_socket_experiment)
    val host = intent.extras.getString("host").trim()

    trainManager().connectAndListen(TrainServer(host)) { command ->
      when (command) {
        is Command.TrainInformation -> {
          speed_label.setText("${command.speed} km/h")
          }
        is Command.TrainList -> if (trainManager().train == null) showTrainListDialog(command.trains)
        else -> {
          Log.w("TRAIN", "Unknown command: $command")
          }
        }
      }

    speedButton.setOnClickListener {
      trainManager().send(Command.TrainControl(targetSpeedText.text.toString().toIntOrNull() ?: 0))
      }

    brakeButton.setOnClickListener {
      trainManager().send(Command.TrainBreak)
      }

    }

    fun connect(train: Train) {
      trainManager().send(Command.Connect(train.id))
      trainManager().train = train
      }

    fun showTrainListDialog(trains: Collection<Train>) {
      val builder = AlertDialog.Builder(this)
      val trainArray = trains.toTypedArray()
      val trainTexts = trains.map { "${it.id} has driver ${it.driver.id}" }.toTypedArray()
      with (builder) {
        title = "Choose train"
        setItems(trainTexts) { dialog, index ->
          val train = trainArray[index]
          if (train.driver.id == 0) connect(train)
          }
        create().show()
        }
      }

  }
