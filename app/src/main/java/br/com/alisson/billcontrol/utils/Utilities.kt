package br.com.alisson.billcontrol.utils

import android.os.SystemClock
import android.widget.Toast
import br.com.alisson.billcontrol.MyApplication

object Utilities{
    private var lastClickTime = 0L
    fun runOnUIThread(runnable: Runnable, delay: Long = 0) {
        if (delay == 0L) {
            MyApplication.applicationHandler.post(runnable)
        } else {
            MyApplication.applicationHandler.postDelayed(runnable, delay)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(
            MyApplication.appContext!!,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun preventingClick(click:Int = 1000): Boolean{
        if ((SystemClock.elapsedRealtime() - lastClickTime) < click){
            return true
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return false
    }
}