package info.itseminar.lego

import android.content.Intent
import android.os.Build.VERSION_CODES.M
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import info.itseminar.lego.Model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var context = this
        var db = DatabaseHelper(context)
        var userName = edittext_username
        var passWord = edittext_password
        var bLogin = button_login
        var mRegister = textview_register

        mRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        bLogin.setOnClickListener {
            var inputUser = User(userName.text.toString(), passWord.text.toString())
            var user = db.validateUser(inputUser)

            if(user != null){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", user.userName)
                startActivity(intent)
                // same as onDestroy()
                finish()
            }
        }
    }

}
