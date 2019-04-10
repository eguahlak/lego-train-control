package info.itseminar.lego

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socket_experiment_button.setOnClickListener {
            startActivity(Intent(this, SocketExperimentActivity::class.java))
            }
        trainButton.setOnClickListener {
            startActivity(Intent(this, TrainGui::class.java))
            }
        }

    }
