package br.com.alisson.billcontrol.utils

import java.util.*

object DateUtils {

    const val ADD = 1
    const val MINUS = -1

    fun manageMonthCalendar(month: Date, type: Int, days: Int = 1): Calendar {
        val qnt = days * type
        val tempCal = Calendar.getInstance()
        tempCal.time = month
        val mm = tempCal.get(Calendar.MONTH)
        val yer = tempCal.get(Calendar.YEAR)

        tempCal.set(yer, mm, 1, 0, 0, 0)
        tempCal.add(Calendar.MONTH, qnt)
        return tempCal
    }

    fun manageDaysCalendar(month: Date, type: Int, days: Int = 1): Calendar {
        val qnt = days * type
        val tempCal = Calendar.getInstance()
        tempCal.time = month
        val mm = tempCal.get(Calendar.MONTH)
        val yer = tempCal.get(Calendar.YEAR)
        val day = tempCal.get(Calendar.DAY_OF_MONTH)

        tempCal.set(yer, mm, day, 0, 0, 0)
        tempCal.add(Calendar.DAY_OF_MONTH, qnt)
        return tempCal
    }
}