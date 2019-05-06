package info.itseminar.lego.protocol

import android.app.Application

class TrainApplication() : Application() {
  //var trainManager: TrainManager = SocketTrainManager()
  var trainManager: TrainManager = DatagramTrainManager()
  }