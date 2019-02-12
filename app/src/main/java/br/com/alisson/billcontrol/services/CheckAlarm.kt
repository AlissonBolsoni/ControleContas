package br.com.alisson.billcontrol.services

import android.content.Context
import android.util.Log
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.Consts
import br.com.alisson.billcontrol.utils.DateUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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

                val bills = ArrayList<ObBill>()
                val reference = FirebaseConfiguration.getFirebaseDatabase()
                    .child(Consts.FIREBASE_BILL)
                    .child(PreferencesConfig(context).getUserAuthId())

                reference.orderByChild("expirationDate")
                    .startAt(today.time.toDouble())
                    .endAt(maxDate.time.toDouble())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            bills.clear()
                            for (data in p0.children) {
                                val bill = data.getValue(ObBill::class.java)
                                if (bill != null)
                                    bills.add(bill)

                            }
                        }
                    })

//                val realm = Realm.getDefaultInstance()
//                val payment: Long? = null
//                val bills =
//                    realm.where(ObBill::class.java).between("expirationDate", today.time, maxDate.time)
//                        .equalTo("paymentDate", payment).findAll()

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