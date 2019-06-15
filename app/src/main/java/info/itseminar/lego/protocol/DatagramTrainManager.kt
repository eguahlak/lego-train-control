package info.itseminar.lego.protocol

import android.content.Context
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.UnsupportedOperationException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
//responsible for transmitting connection to train
class DatagramTrainManager() : TrainManager {
  val socket = DatagramSocket(4711)
  //defined later on
  lateinit var server: TrainServer
  override var train: Train? = null

  val connected: Boolean
    get() = train != null

  @Deprecated("Since 2019-06-14", replaceWith = ReplaceWith("connectAndListen"))
  override fun connect(config: TrainConfig, handle: (Boolean) -> Unit) {
    throw UnsupportedOperationException()
    }

  override fun connectAndListen(server: TrainServer, listener: (Command) -> Unit) {
    Log.d("TRAIN", "Connecting and listening")
    this.server = server
    ConnectAndReceiveTask(this, listener).executeOnExecutor(THREAD_POOL_EXECUTOR, server)
    }

  @Deprecated("Since 2019-06-14", replaceWith = ReplaceWith("connectAndListen"))
  override fun setOnInformation(handle: (Command) -> Unit) {
    throw UnsupportedOperationException()
    }
  //sends commands to the train
  override fun send(command: Command, handle: (String) -> Unit) {
    SendTask(this, handle).executeOnExecutor(THREAD_POOL_EXECUTOR, command)
    }
  //send task extends asynctask
  class SendTask(val manager: DatagramTrainManager, val handle: (String) -> Unit) : AsyncTask<Command, String, Boolean>() {
  //works in background on a different thread
    override fun doInBackground(vararg commands: Command): Boolean {
      Log.d("TRAIN", "Send task started with ${commands.size} commands")
      val server = manager.server
      val socket = manager.socket
      for (command in commands) {
        try {
          Log.d("TRAIN", "Sending $command")
          val output = ByteArrayOutputStream(256)
          command.to(output)
          val buffer = output.toByteArray()
          val datagram = DatagramPacket(buffer, buffer.size, InetAddress.getByName(server.host), server.port)
          socket.send(datagram)
          //if new commands are send publish them
          publishProgress("$command send")
          }
        catch (e: Exception) {
          Log.e("TRAIN", "Error sending $command: ${e.message}")
          publishProgress("Error handling $command")
          return false
          }
        }
      return true
      }
  //update data
    override fun onProgressUpdate(vararg values: String) {
      for (value in values) handle(value)
      }

    }
  //responsible for reciving data from train extends async task to run in background
  class ConnectAndReceiveTask(val manager: DatagramTrainManager, val handle: (Command) -> Unit) : AsyncTask<TrainServer, Command, Boolean>() {

    //listens for updates from the train
    override fun doInBackground(vararg servers: TrainServer): Boolean {
      Log.d("TRAIN", "Connect and Receive task started")
      for (server in servers) {
        Log.d("TRAIN", "Trying to connect to $server")
        try {
          val socket = manager.socket
          val buffer = ByteArray(256)
          val input = ByteArrayInputStream(buffer)
          val datagram = DatagramPacket(buffer, buffer.size)
          //always true
          while (input != null) {
            socket.receive(datagram)
            input.reset()
            val command = Command.from(input)
            // Log.d("TRAIN", "Received $command")
            if (command is Command.Nothing) {
              Log.d("TRAIN", "Nothing received, disconnecting...")
              return true
              }
            publishProgress(command)
            }
          return true
          }
        catch (e: Exception) {
          Log.e("TRAIN", "Can't connect to ${server.host} on ${server.port}")
          return false
          }
        }
      return true
      }
    //updates data
    override fun onProgressUpdate(vararg commands: Command) {
      for (command in commands) handle(command)
      }

    }

  }

