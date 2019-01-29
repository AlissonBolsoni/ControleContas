package br.com.alisson.billcontrol.utils

import java.text.DecimalFormat
import java.util.*

object MoneyUtil {

    private const val PT ="pt"
    private const val BR ="br"
    private const val CURRENT ="R$"
    private const val WANTED ="R$ "

    fun formatBR(valor: Float): String{
        val format = DecimalFormat.getCurrencyInstance(Locale(PT, BR))
        return (format.format(valor)).replace(CURRENT, WANTED)
    }

}