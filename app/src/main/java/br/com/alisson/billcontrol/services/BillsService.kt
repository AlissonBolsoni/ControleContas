package br.com.alisson.billcontrol.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import br.com.alisson.billcontrol.MyApplication
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.Consts
import br.com.alisson.billcontrol.utils.MD5
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BillsService : Service(), BroadcastInterfaceCallback {

    private lateinit var alarmService: AlarmService
    private lateinit var localBroadcast: BillBroadcast
    private var checkAlarm: CheckAlarm? = null

    override fun onCreate() {
        super.onCreate()
        localBroadcast = BillBroadcast.register(this, this, BillBroadcast.ACTION_ALARM)
        alarmService = AlarmService(this)
//        Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show()
        Log.i("BillsService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("BillsService", "onStartCommand -> $startId")
        MyApplication.serviceOn = true

        if (this.checkAlarm == null) {
            this.checkAlarm = CheckAlarm(this, this.alarmService)
            this.checkAlarm!!.startSafe()
        }

        val sp = MyApplication.singleton.sp!!
        val userPair = sp.getUserIsAlreadyConnected()
        if (userPair != null)
            MyApplication.singleton.loginOrConnectFirebase(userPair.first, userPair.second)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmService.stopAlarm()
        BillBroadcast.unregister(this, this.localBroadcast)
        this.checkAlarm?.stopSafe()
        this.checkAlarm = null
//        Toast.makeText(this, getString(R.string.notifications_disabled), Toast.LENGTH_SHORT).show()

        MyApplication.serviceOn = false
        val intent = Intent("br.com.alisson.billcontrol.services.start")
        sendBroadcast(intent)


        Log.i("BillsService", "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun alarmBroadcastCallBack(bills: ArrayList<ObBill>) {
        NotificationService(this).notify(bills)
    }

}
