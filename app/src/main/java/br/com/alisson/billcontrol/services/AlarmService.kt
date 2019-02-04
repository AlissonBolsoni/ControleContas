package br.com.alisson.billcontrol.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.services.broadcasts.AlarmBroadcast
import br.com.alisson.billcontrol.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

class AlarmService(private val context: Context) {

    private var alarmManager: AlarmManager = this.context.getSystemService(ALARM_SERVICE) as AlarmManager
    private var pendingIntent: PendingIntent? = null

    fun createAlarm(bills: List<ObBill>) {

        //Criando a Intent
        val list = ArrayList<String>()

        for (bill in bills)
            list.add(bill.id.toString())

        val it = Intent(AlarmBroadcast.ACTION)
        it.putExtra(AlarmBroadcast.PARAM_BILLS, list)

        this.pendingIntent = PendingIntent.getBroadcast(
            this.context,
            0,
            it,
            0
        )

        val today = Calendar.getInstance()
        val hour = today.get(Calendar.HOUR_OF_DAY)

        var time = if (hour < 7)
            DateUtils.manageDaysCalendar(today.time, DateUtils.ADD, 0)
        else
            DateUtils.manageDaysCalendar(today.time, DateUtils.ADD)

        val year = time.get(Calendar.YEAR)
        val month = time.get(Calendar.MONTH)
        val day = time.get(Calendar.DAY_OF_MONTH)

        time = Calendar.getInstance()
        time.set(year, month, day, 7, 0, 0)

        Log.i("BillsService","Alarme ativado para: ${time.time}")
        alarmManager.set(AlarmManager.RTC_WAKEUP, time.time.time, this.pendingIntent)
    }

    fun stopAlarm() {
        Log.i("Alarme", "Alarme finalizado!")
        if (this.pendingIntent != null)
            alarmManager.cancel(this.pendingIntent)
    }

}