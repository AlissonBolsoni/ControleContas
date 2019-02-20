package br.com.alisson.billcontrol.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.tasks.GetBillsAsync
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(), BroadcastInterfaceCallback {

    private lateinit var broadcast: BillBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this).load(R.drawable.load).into(splash_image)
    }

    override fun onResume() {
        super.onResume()
        broadcast = BillBroadcast.register(this, this, BillBroadcast.ACTION_DATABASE_CHANGE)
        GetBillsAsync().execute()
    }

    override fun onPause() {
        super.onPause()
        BillBroadcast.unregister(this, broadcast)
    }

    override fun downloadFinished() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
