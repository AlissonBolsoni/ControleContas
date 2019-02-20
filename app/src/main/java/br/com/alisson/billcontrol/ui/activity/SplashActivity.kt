package br.com.alisson.billcontrol.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.tasks.GetBillsAsync
import br.com.alisson.billcontrol.eventbus.EventLoad
import com.bumptech.glide.Glide
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this).load(R.drawable.load).into(splash_image)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        GetBillsAsync().execute()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    open fun onEvent(eventLoad: EventLoad){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
