package info.itseminar.lego.protocol

import android.app.Application
import info.itseminar.lego.TrainGui

class TrainApplication() : Application() {
  //var trainManager: TrainManager = SocketTrainManager()
  var trainManager: TrainManager = DatagramTrainManager()
  //var trainManager: TrainManager = TrainGui()
  }