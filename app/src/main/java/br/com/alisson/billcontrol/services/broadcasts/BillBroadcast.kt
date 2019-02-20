package br.com.alisson.billcontrol.services.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import br.com.alisson.billcontrol.data.models.ObBill

class BillBroadcast private constructor(private val callback: BroadcastInterfaceCallback) : BroadcastReceiver() {

    companion object {
        const val ACTION_ALARM = "br.com.alisson.billcontrol.services.broadcasts.BillBroadcast.action.ALARM"
        const val PARAM_BILLS = "PARAM_BILLS"

        const val ACTION_DATABASE_CHANGE = "br.com.alisson.billcontrol.services.broadcasts.BillBroadcast.action.DATABASE_CHANGE"

        fun register(context: Context, callback: BroadcastInterfaceCallback, action: String): BillBroadcast {
            val intentFilter = IntentFilter(action)
            val alarmBroadcast = BillBroadcast(callback)
            val localBroadcastManager = LocalBroadcastManager.getInstance(context)
            localBroadcastManager.registerReceiver(alarmBroadcast, intentFilter)
            return alarmBroadcast
        }

        fun unregister(context: Context, billBroadcast: BillBroadcast) {
            val localBroadcastManager = LocalBroadcastManager.getInstance(context)
            localBroadcastManager.unregisterReceiver(billBroadcast)
        }

        fun notify(context: Context, action: String, param: String?, obj: ArrayList<ObBill>?) {
            val localBroadcastManager = LocalBroadcastManager.getInstance(context)
            val intentBroadcast = Intent(action)

            if (param != null) {
                intentBroadcast.putExtra(param, obj)
            }

            localBroadcastManager.sendBroadcast(intentBroadcast)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {

        when(intent.action){

            ACTION_ALARM ->{
                val bills = intent.getSerializableExtra(PARAM_BILLS) as ArrayList<ObBill>
                this.callback.alarmBroadcastCallBack(bills)
            }

            ACTION_DATABASE_CHANGE ->{
                this.callback.downloadFinished()
            }

        }


    }

}
