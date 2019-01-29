package br.com.alisson.billcontrol.utils

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.provider.Settings.Global.getString
import android.support.v4.content.ContextCompat
import android.widget.TextView
import br.com.alisson.billcontrol.R
import java.util.*


object SetCalendarToView {

    fun setCalendar(context: Context, textView: TextView){

        val myCalendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            textView.text = Formats.SDF.format(myCalendar.time.time)
        }

        val dialog = DatePickerDialog(context, R.style.datepicker, date, myCalendar
            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH))

        textView.setOnClickListener {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel)) {
                    _, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    textView.text = ""
                }
            }
            dialog.show()
        }
    }

}