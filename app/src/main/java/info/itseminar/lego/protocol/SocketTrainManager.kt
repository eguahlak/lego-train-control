package info.itseminar.lego.protocol

import android.content.Context
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.util.Log
import java.net.Socket

class SocketTrainManager() : TrainManager {
  var socket: Socket? = null
  override var train: Train? = null

  val connected: Boolean
    get() = train != null

  @Deprecated("Since 2019-06-14", replaceWith = ReplaceWith("connectAndListen"))
  override fun connect(config: TrainConfig, handle: (Boolean) -> Unit) {
    Log.d("TRAIN", "Connecting")
    ConnectTask(this, handle).executeOnExecutor(THREAD_POOL_EXECUTOR, config)
    }

  override fun connectAndListen(server: TrainServer, listener: (Command) -> Unit) {
    Log.d("TRAIN", "Connecting and listening")
    ConnectAndReceiveTask(this, listener).executeOnExecutor(THREAD_POOL_EXECUTOR, server)
    }

  @Deprecated("Since 2019-06-14", replaceWith = ReplaceWith("connectAndListen"))
  override fun setOnInformation(handle: (Command) -> Unit) {
    ReceiveTask(this, handle).executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

  override fun send(command: Command, handle: (String) -> Unit) {
    SendTask(this, handle).executeOnExecutor(THREAD_POOL_EXECUTOR, command)
    }

  class ConnectTask(val manager: SocketTrainManager, val handle: (Boolean) -> Unit) : AsyncTask<TrainConfig, Void, Boolean>() {

    override fun doInBackground(vararg configs: TrainConfig): Boolean {
      Log.d("TRAIN", "Connect task: started with ${configs.size} configurations")
      for (config in configs) {
        Log.d("TRAIN", "Connect task: ${config.host} ${config.port} ${config.trainId}")
        try {
          val socket = Socket(config.host, config.port)
          Command.Connect(config.trainId).to(socket.getOutputStream())
          manager.socket = socket
          return true
          }
        catch (e: Exception) {
          Log.e("TRAIN", "Can't connect to ${config.host} on ${config.port}")
          return false
          }
        }
      return true
      }

    override fun onPostExecute(result: Boolean) {
      handle(result)
      }

    }

  class SendTask(val manager: SocketTrainManager, val handle: (String) -> Unit) : AsyncTask<Command, String, Boolean>() {

    override fun doInBackground(vararg commands: Command): Boolean {
      Log.d("TRAIN", "Send task started with ${commands.size} commands")
      for (command in commands) {
        try {
          Log.d("TRAIN", "Sending $command")
          val socket = manager.socket ?: throw Exception("Cannot retreive socket")
          command.to(socket.getOutputStream())
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

    override fun onProgressUpdate(vararg values: String) {
      for (value in values) handle(value)
      }

    }

  class ReceiveTask(val manager: SocketTrainManager, val listener: (Command) -> Unit) : AsyncTask<Void, Command, Boolean>() {

    override fun doInBackground(vararg nothing: Void?): Boolean {
      Log.d("TRAIN", "Receive task started")
      try {
        val socket = manager.socket ?: throw Exception("Cannot retreive socket")
        val input = socket.getInputStream()
        while (input != null) {
          val command = Command.from(input)
          if (command is Command.Nothing) {
            Log.d("TRAIN", "Nothing received")
            return true
            }
          publishProgress(command)
          }
        return true
        }
      catch (e: Exception) {
        Log.e("TRAIN", "Information receive error: ${e.message}")
        return false
        }

      }

    override fun onProgressUpdate(vararg commands: Command) {
      for (command in commands) listener(command)
      }

    }


  class ConnectAndReceiveTask(val manager: SocketTrainManager, val handle: (Command) -> Unit) : AsyncTask<TrainServer, Command, Boolean>() {

    override fun doInBackground(vararg servers: TrainServer): Boolean {
      Log.d("TRAIN", "Connect and Receive task started")
      for (server in servers) {
        Log.d("TRAIN", "Trying to connect to $server")
        try {
          val socket = Socket(server.host, server.port)
          manager.socket = socket
          Log.d("TRAIN", "Connected to $server, starts listening")
          val input = socket.getInputStream()
          while (input != null) {
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

    override fun onProgressUpdate(vararg commands: Command) {
      for (command in commands) handle(command)
      }

    }

  }

