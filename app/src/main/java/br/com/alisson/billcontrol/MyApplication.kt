package br.com.alisson.billcontrol

import android.app.Application
import android.content.Intent
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.BillsService
import br.com.alisson.billcontrol.utils.ServiceUtils
import io.realm.Realm

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        if (PreferencesConfig(this).isEnableNotification())
            ServiceUtils.startService(this)
    }

}