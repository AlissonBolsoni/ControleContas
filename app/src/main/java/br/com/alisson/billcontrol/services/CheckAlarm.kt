package br.com.alisson.billcontrol.services

import android.content.Context
import android.util.Log
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.DateUtils
import io.realm.Realm
import java.util.*
import java.util.concurrent.TimeUnit

class CheckAlarm(private val context: Context, private val alarmService: AlarmService) : Runnable {

    companion object {
        private const val RUNNING = 1
        private const val STOP = 2
        private const val STOPPING = 3
    }

    private val sp = PreferencesConfig(context)
    private var status = STOP

    fun startSafe() {
        Log.i("BillsService", "startSafe")
        if (this.status == STOP) {
            this.status = RUNNING
            Thread(this).start()
        } else {
            this.status = RUNNING
        }
    }

    fun stopSafe() {
        Log.i("BillsService", "stopSafe")
        if (status == STOP) return

        this.status = STOPPING
    }

    override fun run() {
        Log.i("BillsService", "run $status")
        while (this.status == RUNNING) {
            if (this.sp.isEnableNotification()) {
                val days = this.sp.getDaysBeforeNotification()
                val today = DateUtils.manageDaysCalendar(Calendar.getInstance().time, DateUtils.ADD, 0).time

                val maxCal = DateUtils.manageDaysCalendar(today, DateUtils.ADD, days)
                val maxDate = maxCal.time

                val realm = Realm.getDefaultInstance()
                val payment: Long? = null
                val bills =
                    realm.where(ObBill::class.java).between("expirationDate", today.time, maxDate.time)
                        .equalTo("paymentDate", payment).findAll()

                Log.i("BillsService", "run -> $bills")
                if (bills.size > 0) {
                    alarmService.createAlarm(bills)
                }
            }
            Thread.sleep(TimeUnit.HOURS.toMillis(3))
        }
        this.status = STOP
    }
}