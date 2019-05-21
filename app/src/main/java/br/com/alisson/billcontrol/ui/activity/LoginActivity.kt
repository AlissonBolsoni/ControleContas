package br.com.alisson.billcontrol.ui.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.alisson.billcontrol.MyApplication
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.models.ObUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val LOGIN_REQUEST = 759
        const val REGISTER_REQUEST = 153
        const val OBUSER = "OBUSER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sp = MyApplication.singleton.sp!!
        val userPair = sp.getUserIsAlreadyConnected()
        if (userPair != null)
            executeLogin(userPair.first, userPair.second)
        else {
            login_btn.setOnClickListener {
                onLogin()
            }
        }
    }

    private fun onLogin() {
        val username = edt_username.text.toString().trim()
        val password = edt_password.text.toString().trim()

        imp_username.error = if (username.isEmpty()) getString(R.string.email_is_empty) else null
        imp_password.error = if (password.isEmpty()) getString(R.string.password_is_empty) else null

        if (imp_username.error.isNullOrBlank() && imp_password.error.isNullOrBlank()) {
            executeLogin(username, password)
        }
    }

    private fun goToMainAct() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun executeLogin(username: String, password: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(SplashActivity.USER_EMAIL, username)
        intent.putExtra(SplashActivity.USER_PASSWORD, password)
        startActivityForResult(intent, LOGIN_REQUEST)
    }

    private fun registerUser(obUser: ObUser) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(SplashActivity.USER_REGISTER, obUser)
        startActivityForResult(intent, REGISTER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == LOGIN_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    goToMainAct()
                } else {
                    val obUser = data.getSerializableExtra(OBUSER) as ObUser
                    login_btn.text = "Cadastrar"
                    imp_name.visibility = View.VISIBLE

                    login_btn.setOnClickListener {
                        val name = edt_name.text.toString().trim()
                        imp_name.error = if (name.isEmpty()) getString(R.string.name_empty) else null

                        if (imp_name.error.isNullOrBlank()) {
                            obUser.name = name
                            registerUser(obUser)
                        }
                    }
                }
            }else if (requestCode == REGISTER_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    goToMainAct()
                } else {

                }
            }
        }
    }


}
