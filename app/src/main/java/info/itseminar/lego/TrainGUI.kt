package info.itseminar.lego

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.trainManager
import kotlinx.android.synthetic.main.activity_traingui.*
import info.itseminar.lego.protocol.Train
import info.itseminar.lego.protocol.TrainServer


class TrainGui : AppCompatActivity() {

  var speed = 0
  var targetspeed = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_traingui)
    val host = intent.extras.getString("host").trim()

    // AKA: use property syntax when possible
    speedometer.isWithTremble = false
    //traffic.

    trainManager().connectAndListen(TrainServer(host)) { command ->
      when (command) {
        is Command.TrainInformation -> {
          currentSpeedLabel.setText("${command.speed} km/h")
          speedometer.speedTo(command.speed.toFloat(), 1000)
          distance_to_light.setText("${command.distanceToLight} light years")
          track_id.setText("${command.trackId} trackID")
          command.light
          //println(command.distanceToLight)
        }
        is Command.TrainList -> if (trainManager().train == null) showTrainListDialog(command.trains)
        else -> {
          Log.w("TRAIN", "Unknown command: $command")
        }
      }
    }

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
        driver_id_text.setText("${train.id} has connected")
      }
      create().show()
    }
  }
}
