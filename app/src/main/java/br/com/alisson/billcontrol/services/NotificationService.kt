package br.com.alisson.billcontrol.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.utils.NotificationUtils
import java.lang.Exception
import java.util.*

class NotificationService(private val context: Context) {

    private val mNotificationManager =
        NotificationManagerCompat.from(context)

    fun notify(bills: List<ObBill>) {

        bills.forEachIndexed { index, obBill ->
            toDispatch(obBill, index)
        }
    }

    private fun toDispatch(bill: ObBill, id: Int) {

        val intent = Intent(MainActivity.ACTION_NOTIFICATION)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent
            .getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val title: String = bill.description!!

        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val expDate = Calendar.getInstance()
        expDate.time = Date(bill.expirationDate)
        val expDay = expDate.get(Calendar.DAY_OF_YEAR)

        val msg = if ((expDay - today) == 1)
            this.context.getString(R.string.day_left, bill.description!!, (expDay - today))
        else
            this.context.getString(R.string.days_left, bill.description!!, (expDay - today))

        notify(id, title, msg, pendingIntent)
    }

    private fun notify(id: Int, title: String, message: String, pendingIntent: PendingIntent) {
        try {
            val chanelID = "ntalk_channel_01"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationUtils.createNotificationChannel(
                    context,
                    chanelID,
                    title,
                    NotificationManager.IMPORTANCE_HIGH
                )
            }

            val mBuilder = NotificationCompat.Builder(context, chanelID)
            mBuilder.setContentIntent(pendingIntent)
            mBuilder.setVibrate(longArrayOf(0, 500, 200, 500))
            mBuilder.setSmallIcon(R.drawable.home_currency_usd)
            mBuilder.setContentTitle(title)
            mBuilder.setContentText(message)
            mBuilder.priority = NotificationCompat.PRIORITY_MAX
            mBuilder.setAutoCancel(true)
            mBuilder.color = context.resources.getColor(R.color.colorPrimaryLight)
            mBuilder.setColorized(true)

            Log.i("Notificacao", "notitfy")

            mNotificationManager.apply {
                notify(id, mBuilder.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}