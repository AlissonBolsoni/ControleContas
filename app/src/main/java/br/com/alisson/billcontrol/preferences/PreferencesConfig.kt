package br.com.alisson.billcontrol.preferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class PreferencesConfig(context: Context) {

    companion object {
        private const val SP_NAME = "SP_NAME_CONFIG"

        private const val NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED"
        private const val DAYS_BEFORE_NOTIFICATION = "DAYS_BEFORE_NOTIFICATION"
        private const val ALARM_SELECTED = "ALARM_SELECTED"
    }

    private lateinit var sp: SharedPreferences

    init {
        sp = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE)
    }

    fun saveNotificationSharedPreferences(checked: Boolean) {
        val editor = sp.edit()
        editor.putBoolean(NOTIFICATION_ENABLED, checked)
        editor.apply()
    }

    fun isEnableNotification(): Boolean{
        return sp.getBoolean(NOTIFICATION_ENABLED, false)
    }

    fun setDaysBeforeNotification(days: Int){
        val editor = sp.edit()
        editor.putInt(DAYS_BEFORE_NOTIFICATION, days)
        editor.apply()
    }

    fun getDaysBeforeNotification():Int{
        return sp.getInt(DAYS_BEFORE_NOTIFICATION, 0)
    }

    fun getAlarmSelected() = sp.getString(ALARM_SELECTED, "")

    fun setAlarmSelected(alarm: String){
        val editor = sp.edit()
        editor.putString(ALARM_SELECTED, alarm)
        editor.apply()
    }

}
