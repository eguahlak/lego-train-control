package info.itseminar.lego.protocol

interface TrainManager {
    fun setOnInformation(code: TrainManager.(Command) -> Unit = { })
    fun send(command: Command)
    }

class SocketTrainManager(trainId: Int) : TrainManager {
    override fun setOnInformation(code: TrainManager.(Command) -> Unit) {
        TODO("not implemented")
        }

    override fun send(command: Command) {
        TODO("not implemented")
        }

    }

fun trainManager(trainId: Int) = SocketTrainManager(trainId)

