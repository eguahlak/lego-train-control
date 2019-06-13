package info.itseminar.lego

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import info.itseminar.lego.Model.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edittext_password
import kotlinx.android.synthetic.main.activity_login.edittext_username
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var userName = edittext_username
        var passWord = edittext_password
        var cnfPassword = edittext_cnf_password
        var bRegister = button_register
        var mLogin = textview_login

        mLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val context = this

        bRegister.setOnClickListener {
            if(userName.text.toString().length > 0 &&
                passWord.text.toString().length > 0 &&
                    cnfPassword.text.toString().length > 0) {
                var user = User(userName.text.toString(), passWord.text.toString())
                var db = DatabaseHelper(context)
                db.createUser(user)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(context, "Please Fill All Data's", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
