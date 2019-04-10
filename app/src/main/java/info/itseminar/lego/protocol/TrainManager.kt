package info.itseminar.lego.protocol

interface TrainManager {
  fun connect(config: TrainConfig, handle: (Boolean) -> Unit = { })
  fun setOnInformation(handle: (Command) -> Unit = { })
  fun send(command: Command, handle: (String) -> Unit = { })
  }

class TrainConfig(val host: String, val port: Int, val trainId: Int)

