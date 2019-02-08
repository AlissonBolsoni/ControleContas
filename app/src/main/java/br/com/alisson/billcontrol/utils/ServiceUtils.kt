package br.com.alisson.billcontrol.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import br.com.alisson.billcontrol.services.BillsService

object ServiceUtils {

    fun startService(context: Context){
        ContextCompat.startForegroundService(context, Intent(context, BillsService::class.java))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startService(Intent(context, BillsService::class.java))
        }
    }

}
