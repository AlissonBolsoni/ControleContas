package br.com.alisson.billcontrol.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.models.ObUser
import br.com.alisson.billcontrol.data.tasks.GetBillsAsync
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(), BroadcastInterfaceCallback {

    companion object {
        const val USER_EMAIL = "USER_EMAIL"
        const val USER_PASSWORD = "USER_PASSWORD"
        const val USER_REGISTER = "USER_REGISTER"
    }

    private lateinit var broadcast: BillBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        Glide.with(this).load(R.drawable.load).into(splash_image)
    }

    override fun onResume() {
        super.onResume()
        broadcast = BillBroadcast.register(this, this, BillBroadcast.ACTION_DATABASE_CHANGE)

        if (intent.hasExtra(USER_EMAIL) && intent.hasExtra(USER_PASSWORD)) {
            val email = intent.getStringExtra(USER_EMAIL)
            val password = intent.getStringExtra(USER_PASSWORD)
            GetBillsAsync(GetBillsAsync.LOGIN) {
                val intent = Intent()
                intent.putExtra(LoginActivity.OBUSER, it)
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }.execute(email, password)
        }else if (intent.hasExtra(USER_REGISTER)){
            val obUser = intent.getSerializableExtra(USER_REGISTER) as ObUser
            GetBillsAsync(GetBillsAsync.REGISTER) {
                val intent = Intent()
                intent.putExtra(LoginActivity.OBUSER, it)
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }.execute(obUser)
        }
    }

    override fun onPause() {
        super.onPause()
        BillBroadcast.unregister(this, broadcast)
    }

    override fun downloadFinished() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
