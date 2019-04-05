package info.itseminar.lego.protocol

import java.io.*

fun InputStream.readInt(): Int {
    var value = 0
    for (index in 0..3) value = value or (this.read() shl (8*index))
    return value
}

fun OutputStream.writeInt(value: Int) {
    for (index in 0..3) this.write(value shr (8*index) and 255)
}

class CommandException(message: String) : Exception(message)

sealed class Command {
    companion object {
        const val NOTHING = -1
        const val CONNECT = 1
        const val TRAIN_INFORMATION = 2
        const val TRAIN_CONTROL = 3
        const val NOT_CONNECTED = 101
        const val ALREADY_CONNECTED = 102

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
                NOT_CONNECTED -> {
                    if (length != 2) throw CommandException("NOT_CONNECTED expected to be 2 was $length")
                    return NotConnected
                }
                ALREADY_CONNECTED -> {
                    if (length != 2) throw CommandException("ALREADY_CONNECTED expected to be 2 was $length")
                    return AlreadyConnected
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
        override fun toString() = "TRAIN_INFORMATION speed: $speed, track: #$trackId, distance: $distanceToLight with $light"
    }

    class TrainControl(val speed: Int) : Command() {
        override fun to(output: OutputStream) {
            output.write(6)
            output.write(TRAIN_CONTROL)
            output.writeInt(speed)
            output.flush()
        }
        override fun toString() = "TRAIN_CONTROL speed: $speed"
    }

}


fun main(args: Array<String>) {
    val input = PipedInputStream(1024)
    val output = PipedOutputStream(input)

    Command.Connect(17).to(output)
    Command.TrainControl(1050).to(output)
    Command.TrainInformation(500, 17, 6, 1u).to(output)
    Command.TrainInformation(600, 18, 5, 1u).to(output)
    Command.TrainInformation(750, 19, 4, 1u).to(output)

    while (input.available() > 0) {
        println(Command.from(input))
    }

}
