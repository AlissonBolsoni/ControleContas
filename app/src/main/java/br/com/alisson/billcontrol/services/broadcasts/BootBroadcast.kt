package br.com.alisson.billcontrol.services.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.ServiceUtils

class BootBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BillsService","BootBroadcast")
            if (PreferencesConfig(context).isEnableNotification()){
                ServiceUtils.startService(context)
            }
        }
    }


}