package br.com.alisson.billcontrol.utils

import java.text.SimpleDateFormat
import java.util.*

object Formats {

    val SDF = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val SDF_HOURS = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault())

    val SDF_MONTH_YEAR = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

}