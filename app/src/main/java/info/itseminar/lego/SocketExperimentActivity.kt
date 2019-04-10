package info.itseminar.lego

import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import info.itseminar.lego.protocol.Command
import kotlinx.android.synthetic.main.activity_socket_experiment.*
import java.io.IOException
import java.net.Socket

class SocketExperimentActivity : AppCompatActivity() {
    var trainSocket: Socket? = null
    var informationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_experiment)
        TrainConnectTask(this).executeOnExecutor(THREAD_POOL_EXECUTOR, TrainConfig("10.50.130.30", 4711, 17))
        listenButton.setOnClickListener {
            TrainInputTask(this).executeOnExecutor(THREAD_POOL_EXECUTOR, trainSocket)
            }
        speedButton.setOnClickListener {
            if (trainSocket == null) toast("Socket not initialized!")
            val speed = targetSpeedText.text.toString().toIntOrNull() ?: 123
            toast("setting speed to $speed")
            val command = Command.TrainControl(speed)
            TrainControlTask(this).executeOnExecutor(THREAD_POOL_EXECUTOR, trainSocket!! to command)
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        }

    class TrainConnectTask(val activity: SocketExperimentActivity) : AsyncTask<TrainConfig, Void, Socket?>() {
        override fun doInBackground(vararg configs: TrainConfig): Socket? {
            if (configs.size == 0) return null
            val config = configs[0]
            try {
                val socket = Socket(config.host, config.port)
                Command.Connect(config.trainId).to(socket.getOutputStream())
                return socket
                }
            catch (e: IOException) {
                Log.e("TRAIN", "Connect error: ${e.message}")
                return null
                }
            }

        override fun onPostExecute(result: Socket?) {
            activity.trainSocket = result
            activity.speedButton.setText("Set Speed")
            }

        }


    class TrainInputTask(val activity: SocketExperimentActivity) : AsyncTask<Socket, Command, Boolean>() {
        override fun doInBackground(vararg sockets: Socket): Boolean {
            if (sockets.size == 0) return false
            val socket = sockets[0]
            try {
                val input = socket.getInputStream()
                while (true) {
                    val command = Command.from(input)
                    if (command is Command.Nothing) {
                        Log.d("TRAIN", "Nothing received")
                        return true
                        }
                    publishProgress(command)
                    }
                return true
                }
            catch (e: IOException) {
                Log.e("TRAIN", "Information error: ${e.message}")
                return false
                }

            }

        override fun onProgressUpdate(vararg commands: Command) {
            for (command in commands) {
                when (command) {
                    is Command.TrainInformation -> {
                        activity.speed_label.setText("${activity.informationCount++}: ${command.speed}")
                        }
                    else -> {
                        Log.w("TRAIN", "Unexpected command $command")
                        }
                    }
                }

            }

        }

    class TrainControlTask(val activity: SocketExperimentActivity) : AsyncTask<Pair<Socket,Command>,Void,Boolean>() {
        override fun doInBackground(vararg pairs: Pair<Socket,Command>): Boolean {
            Log.d("TRAIN", "Controlling train")
            try {
                for (pair in pairs) {
                    pair.second.to(pair.first.getOutputStream())
                    break
                    }
                }
            catch (e: IOException) {
                Log.e("TRAIN", "Control error: ${e.message}")
                return false
                }
            return true
            }

        }

    }

class TrainConfig(val host: String, val port: Int, val trainId: Int)