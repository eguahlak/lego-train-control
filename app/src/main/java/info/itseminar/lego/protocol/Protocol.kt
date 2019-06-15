package info.itseminar.lego.protocol

import java.io.*

class CommandException(message: String) : Exception(message)

sealed class Command {
  companion object {
    const val NOTHING = -1
    const val CONNECT = 1
    const val TRAIN_INFORMATION = 2
    const val TRAIN_CONTROL = 3
    const val TRAIN_BRAKE = 4
    const val TRAIN_LIST = 100
    const val NOT_CONNECTED = 101
    const val ALREADY_CONNECTED = 102

    //The function "from" is called upon from the DatagramTrainManager
    //according to the input it recives from the server(train) it will return a command from that
    fun from(input: InputStream): Command {
      val length = input.read()
      val commandKey = input.read()
      when (commandKey) {
        NOTHING -> return Nothing
        CONNECT -> {
          if (length != 6) throw CommandException("CONNECT expected to be 6 was $length")
          val trainId = input.readInt()
          return Connect(trainId)
        }
        TRAIN_INFORMATION -> {
          if (length != 15) throw CommandException("TRAIN_INFORMATION expected to be 15 was $length")
          val speed = input.readInt()
          val trackId = input.readInt()
          val distanceToLight = input.readInt()
          val light = input.read()
          return TrainInformation(
            speed,
            trackId,
            distanceToLight,
            light.toUByte()
          )
        }
        TRAIN_CONTROL -> {
          if (length != 6) throw CommandException("TRAIN_CONTROL expected to be 6 was $length")
          val speed = input.readInt()
          return TrainControl(speed)
        }
        TRAIN_BRAKE -> {
          if (length != 2) throw CommandException("TRAIN_BRAKE expeted to be 2 was $length")
          return TrainBreak
        }
        NOT_CONNECTED -> {
          if (length != 2) throw CommandException("NOT_CONNECTED expected to be 2 was $length")
          return NotConnected
        }
        ALREADY_CONNECTED -> {
          if (length != 2) throw CommandException("ALREADY_CONNECTED expected to be 2 was $length")
          return AlreadyConnected
        }
        TRAIN_LIST -> {
          val trains = input.readTrains()
          return TrainList(trains)
        }

        else -> throw CommandException("unknown command: $commandKey")
      }
    }
  }

  abstract fun to(output: OutputStream)

  object Nothing : Command() {
    override fun to(output: OutputStream) {}
    override fun toString() = "NOTHING"
  }

  class Connect(val trainId: Int) : Command() {
    override fun to(output: OutputStream) {
      output.write(6)
      output.write(CONNECT)
      output.writeInt(trainId)
      output.flush()
    }

    override fun toString() = "CONNECT #${trainId}"
  }

  object NotConnected : Command() {
    override fun to(output: OutputStream) {
      output.write(2)
      output.write(NOT_CONNECTED)
      output.flush()
    }

    override fun toString() = "NOT_CONNECTED"
  }

  object AlreadyConnected : Command() {
    override fun to(output: OutputStream) {
      output.write(2)
      output.write(ALREADY_CONNECTED)
      output.flush()
    }

    override fun toString() = "NOT_CONNECTED"
  }

  object TrainBreak : Command() {
    override fun to(output: OutputStream) {
      output.write(2)
      output.write(TRAIN_BRAKE)
      output.flush()
    }

    override fun toString() = "TRAIN_BRAKE"
  }

  // en metode der skriver al information omkring toget, skinneId, fart, næste lys.
  class TrainInformation(val speed: Int, val trackId: Int, val distanceToLight: Int, val light: UByte) : Command() {
    override fun to(output: OutputStream) {
      output.write(15)
      output.write(TRAIN_INFORMATION)
      output.writeInt(speed)
      output.writeInt(trackId)
      output.writeInt(distanceToLight)
      output.write(light.toInt())
      output.flush()
    }

    override fun toString() =
      "TRAIN_INFORMATION speed: $speed, track: #$trackId, distance: $distanceToLight with $light"
  }

  //Bruges til at sætte farten på toget.
  class TrainControl(val speed: Int) : Command() {
    override fun to(output: OutputStream) {
      output.write(6)
      output.write(TRAIN_CONTROL)
      output.writeInt(speed)
      output.flush()
    }

    override fun toString() = "TRAIN_CONTROL speed: $speed"
  }

  //bruges til at finde ud af hvor mange tog der er.
  class TrainList(val trains: Collection<Train>) : Command() {
    override fun to(output: OutputStream) {
      output.write(3 + 8*trains.size)
      output.write(TRAIN_LIST)
      output.writeTrains(trains)
      output.flush()
    }

    override fun toString() = "TRAIN_LIST # of trains: ${trains.size}"

  }

}

data class Driver(val id: Int)

data class Train(val id: Int, val driver: Driver)

fun InputStream.readInt(): Int {
  var value = 0
  for (index in 0..3) value = value or (this.read() shl (8 * index))
  return value
}

fun OutputStream.writeInt(value: Int) {
  for (index in 0..3) this.write(value shr (8 * index) and 255)
}

fun InputStream.readDriver() = Driver(this.readInt())

fun InputStream.readTrain() = Train(this.readInt(), this.readDriver())

fun InputStream.readTrains(): List<Train> {
  val size = this.read()
  val trains = mutableListOf<Train>()
  for (i in 1..size) trains.add(this.readTrain())
  return trains
}

fun OutputStream.writeDriver(driver: Driver) {
  this.writeInt(driver.id)
}

fun OutputStream.writeTrain(train: Train) {
  this.writeInt(train.id)
  this.writeDriver(train.driver)
}

fun OutputStream.writeTrains(trains: Collection<Train>) {
  this.write(trains.size)
  trains.forEach { this.writeTrain(it) }
}

