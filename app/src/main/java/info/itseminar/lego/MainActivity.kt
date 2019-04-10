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
            val host = host_input.text.toString()
            val intent = Intent(this, SocketExperimentActivity::class.java)
            intent.putExtra("host", host)
            startActivity(intent)
            }
        /*trainButton.setOnClickListener {
            startActivity(Intent(this, TrainGui::class.java))
            }
*/
        }

    }
