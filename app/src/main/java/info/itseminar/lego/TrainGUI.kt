package info.itseminar.lego

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ContextThemeWrapper
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.trainManager
import kotlinx.android.synthetic.main.activity_traingui.*
import info.itseminar.lego.protocol.Train
import info.itseminar.lego.protocol.TrainServer
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.android.synthetic.main.fragment_speed.*


class TrainGui : AppCompatActivity() {

  var targetspeed = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_traingui)
    val host = intent.extras.getString("host").trim()
    speedometer.setMinMaxSpeed(0F, 300F)
    speedometer.isWithTremble = false

    //val wrapper = ContextThemeWrapper(this, R.style.BBB)
    //changeTheme(wrapper.getTheme())

    trainManager().connectAndListen(TrainServer(host)) { command ->
      when (command) {
        is Command.TrainInformation -> {
          currentSpeedLabel.setText("${targetspeed} km/h")
          speedometer.speedTo(command.speed.toFloat(), 1000)
          //distance_to_light.setText("Distance to next light: ${command.distanceToLight}")
          //track_id.setText("Train running on track: ${command.trackId}")
          changeLight("${command.light}")
          trackNumber("${command.trackId}")
        }
        is Command.TrainList -> if (trainManager().train == null) showTrainListDialog(command.trains)
        else -> {
          Log.w("TRAIN", "Unknown command: $command")
        }
      }
    }



    speedUpButton.setOnClickListener {
      val increase = 50
      targetspeed += increase
      trainManager().send(Command.TrainControl(targetspeed))
      currentSpeedLabel.setText("${targetspeed} km/h")
    }
    speedDownButton.setOnClickListener {
      val decrease = 50
      targetspeed -= decrease
      trainManager().send(Command.TrainControl(targetspeed))
      currentSpeedLabel.setText("${targetspeed} km/h")
    }


    stopButton.setOnClickListener {
      trainManager().send(Command.TrainBreak)
      targetspeed = 0
      currentSpeedLabel.setText("${targetspeed} km/h")
    }
  }
  fun changeTheme(@SuppressLint("SupportAnnotationUsage") @StyleRes theme: Resources.Theme) {
    val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_traffic_light, theme)
    traffic.setImageDrawable(drawable)
  }
  fun changeTrack(@SuppressLint("SupportAnnotationUsage") @StyleRes theme: Resources.Theme) {
    val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_track, theme)
    tracksvg.setImageDrawable(drawable)
  }


    fun connect(train: Train) {
      trainManager().send(Command.Connect(train.id))
      trainManager().train = train
    }

    fun showTrainListDialog(trains: Collection<Train>) {
      val builder = AlertDialog.Builder(this)
      val trainArray = trains.toTypedArray()
      val trainTexts = trains.map { "${it.id} has driver ${it.driver.id}" }.toTypedArray()
      with(builder) {
        title = "Choose train"
        setItems(trainTexts) { dialog, index ->
          val train = trainArray[index]
          if (train.driver.id == 0) connect(train)
          //driver_id_text.setText("You control train number: ${train.id}")
        }
        create().show()
      }
    }

  fun changeLight(light: String) {
    val BBR = ContextThemeWrapper(this, R.style.BBR)
    val BGR = ContextThemeWrapper(this, R.style.BGR)
    val GGB = ContextThemeWrapper(this, R.style.GGB)
    when(light){
      "1" -> changeTheme(BBR.theme)
      "3" -> changeTheme(BGR.theme)
      "6" -> changeTheme(GGB.theme)
    }
  }
  fun trackNumber(trackId: String) {
    val ONE = ContextThemeWrapper(this, R.style.T1)
    val TWO = ContextThemeWrapper(this, R.style.T2)
    val THREE = ContextThemeWrapper(this, R.style.T3)
    val FOUR = ContextThemeWrapper(this, R.style.T4)
    val FIVE = ContextThemeWrapper(this, R.style.T5)
    val SIX = ContextThemeWrapper(this, R.style.T6)
    val SEVEN = ContextThemeWrapper(this, R.style.T7)
    val EIGHT = ContextThemeWrapper(this, R.style.T8)
    val NINE = ContextThemeWrapper(this, R.style.T9)
    val TEN = ContextThemeWrapper(this, R.style.T10)
    val ELEVEN = ContextThemeWrapper(this, R.style.T11)
    val TWELVE = ContextThemeWrapper(this, R.style.T12)

    when(trackId){
      "1" -> changeTrack(ONE.theme)
      "2" -> changeTrack(TWO.theme)
      "3" -> changeTrack(THREE.theme)
      "4" -> changeTrack(FOUR.theme)
      "5" -> changeTrack(FIVE.theme)
      "6" -> changeTrack(SIX.theme)
      "7" -> changeTrack(SEVEN.theme)
      "8" -> changeTrack(EIGHT.theme)
      "9" -> changeTrack(NINE.theme)
      "10" -> changeTrack(TEN.theme)
      "11" -> changeTrack(ELEVEN.theme)
      "12" -> changeTrack(TWELVE.theme)
    }
  }
  }

