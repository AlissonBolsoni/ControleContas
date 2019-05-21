package br.com.alisson.billcontrol.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.BillsService
import br.com.alisson.billcontrol.utils.ServiceUtils
import kotlinx.android.synthetic.main.fragment_config_layout.*

class ConfigFragment: BaseFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sp: PreferencesConfig


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_config_layout, container, false)

        title = getString(R.string.title_config)
        setTitle()

        super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.sp = PreferencesConfig(context!!)
        setDays()

        allowNotification.isChecked = this.sp.isEnableNotification()
        allowNotification.setOnCheckedChangeListener { _, checked ->
            sp.saveNotificationSharedPreferences(checked)
            setDays()
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun setDays() {
        days_before_nofication.text = sp.getDaysBeforeNotification().toString()

        if (this.sp.isEnableNotification()) {
            days_before_nofication.setOnClickListener {
                val builder = AlertDialog.Builder(mainActivity!!)
                builder.setTitle(getString(R.string.quantity_days))
                val viewDialog =
                    LayoutInflater.from(context!!).inflate(R.layout.config_days_before_notification_dialog, null)
                val edit = viewDialog.findViewById<EditText>(R.id.config_days_bef_not_text_input_edit)
                val days = sp.getDaysBeforeNotification().toString()
                edit.setText(days)
                builder.setView(viewDialog)
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    val text = edit.text.trim().toString()
                    if (text.isEmpty())
                        this.sp.setDaysBeforeNotification(0)
                    else {
                        this.sp.setDaysBeforeNotification(text.toInt())
                        days_before_nofication.text = text
                    }
                }
                builder.setNegativeButton(getString(R.string.cancel), null)
                val dialog = builder.create()
                dialog.show()
            }
            config_alarm.text = sp.getAlarmSelected()
        }else {
            days_before_nofication.setOnClickListener(null)
            config_alarm.text = ""
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        config_alarm.text = sp.getAlarmSelected()
    }
}