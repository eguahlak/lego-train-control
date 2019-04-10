package info.itseminar.lego

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.itseminar.lego.protocol.Command
import info.itseminar.lego.protocol.TrainConfig
import kotlinx.android.synthetic.main.activity_socket_experiment.*
import kotlinx.android.synthetic.main.activity_traingui.*
import java.io.IOException
import java.net.Socket

class TrainGui : AppCompatActivity() {
    var trainSocket: Socket? = null
    var speed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traingui)
        TrainGui.TrainConnectTask(this)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, TrainConfig("10.50.130.30", 4711, 17))
        speedUpButton.setOnClickListener {
            if (trainSocket == null) toast("Socket not initialized!")
            val increase = 5
            speed += increase
            toast("setting speed to $speed")
            val command = Command.TrainControl(speed)
            TrainGui.TrainControlTask(this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, trainSocket!! to command)
        }
    }
    class TrainConnectTask(val activity: TrainGui) : AsyncTask<TrainConfig, Void, Socket?>() {
        override fun doInBackground(vararg configs: TrainConfig): Socket? {
            if (configs.size == 0) return null
            val config = configs[0]
            try {
                val socket = Socket(config.host, config.port)
                Command.Connect(config.trainId).to(socket.getOutputStream())
                return socket
            } catch (e: IOException) {
                Log.e("TRAIN", "Connect error: ${e.message}")
                return null
            }
        }
    }
        class TrainControlTask(val activity: TrainGui) : AsyncTask<Pair<Socket,Command>,Void,Boolean>() {
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
