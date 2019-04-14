package info.itseminar.lego.protocol

interface TrainManager {
  var train: Train?
  fun connect(config: TrainConfig, handle: (Boolean) -> Unit = { })
  fun setOnInformation(handle: (Command) -> Unit = { })
  fun send(command: Command, handle: (String) -> Unit = { })
  fun connectAndListen(server: TrainServer, listener: (Command) -> Unit)
  }

data class TrainConfig(val host: String, val port: Int, val trainId: Int)

data class TrainServer(val host: String, val port: Int = 4711)

