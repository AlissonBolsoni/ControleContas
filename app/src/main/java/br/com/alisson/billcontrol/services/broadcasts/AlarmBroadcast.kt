package br.com.alisson.billcontrol.services.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import br.com.alisson.billcontrol.models.ObBill

class AlarmBroadcast(private val callback: AlarmBroadcastInterface) : BroadcastReceiver() {

    companion object {
        const val ACTION = "br.com.alisson.billcontrol.services.broadcasts.AlarmBroadcast.action"
        const val PARAM_BILLS = "PARAM_BILLS"

        fun register(context: Context, callback: AlarmBroadcastInterface): AlarmBroadcast {
            val intentFilter = IntentFilter(ACTION)
            val alarmBroadcast = AlarmBroadcast(callback)
            context.registerReceiver(alarmBroadcast, intentFilter)
            return alarmBroadcast
        }

        fun unregister(context: Context, alarmBroadcast: AlarmBroadcast) {
            context.unregisterReceiver(alarmBroadcast)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        val bills = intent.getSerializableExtra(PARAM_BILLS) as ArrayList<ObBill>
        this.callback.alarmBroadcastCallBack(bills)
    }

}
