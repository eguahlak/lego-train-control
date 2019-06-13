package info.itseminar.lego

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val username = intent.extras.getString("username").trim()
        textView2.setText("Hello ${username} Click Start!")
        imageButton.setOnClickListener {
            val host = host_input.text.toString()
            val intent = Intent(this, TrainGui::class.java)
            intent.putExtra("host", host)
            startActivity(intent)
            }

        }

    }
