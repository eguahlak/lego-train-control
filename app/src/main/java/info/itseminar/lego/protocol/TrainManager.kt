package info.itseminar.lego.protocol

import android.content.Context

interface TrainManager {
  var train: Train?
  fun connect(config: TrainConfig, handle: (Boolean) -> Unit = { })
  fun setOnInformation(handle: (Command) -> Unit = { })
  fun send(command: Command, handle: (String) -> Unit = { })
  fun connectAndListen(server: TrainServer, listener: (Command) -> Unit)
  }

data class TrainConfig(val host: String, val port: Int, val trainId: Int)

data class TrainServer(val host: String, val port: Int = 4711)

fun Context.trainManager(init: TrainManager.() -> Unit = { }): TrainManager {
  val application = this.applicationContext as TrainApplication
  val manager = application.trainManager
  manager.init()
  return manager
}
