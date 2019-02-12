package br.com.alisson.billcontrol.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.broadcasts.AlarmBroadcast
import br.com.alisson.billcontrol.services.broadcasts.AlarmBroadcastInterface

class BillsService : Service(), AlarmBroadcastInterface {

    private lateinit var sp: PreferencesConfig
    private lateinit var alarmService: AlarmService
    private lateinit var localBroadcast: AlarmBroadcast
    private var checkAlarm: CheckAlarm? = null

    override fun onCreate() {
        super.onCreate()
        this.sp = PreferencesConfig(this)
        localBroadcast = AlarmBroadcast.register(this, this)
        alarmService = AlarmService(this)
//        Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show()
        Log.i("BillsService","onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("BillsService","onStartCommand -> $startId")

        if(this.checkAlarm == null){
            this.checkAlarm = CheckAlarm(this, this.alarmService)
            this.checkAlarm!!.startSafe()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmService.stopAlarm()
        AlarmBroadcast.unregister(this, this.localBroadcast)
        this.checkAlarm?.stopSafe()
        this.checkAlarm = null
//        Toast.makeText(this, getString(R.string.notifications_disabled), Toast.LENGTH_SHORT).show()
        Log.i("BillsService","onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun alarmBroadcastCallBack(bills: ArrayList<ObBill>) {
        NotificationService(this).notify(bills)

    }

}
