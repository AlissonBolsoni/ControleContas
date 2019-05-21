package br.com.alisson.billcontrol.data.dao

import android.content.Context
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.data.models.ObUser
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.Consts

object FirebaseDao {

    fun insert(context: Context,obBill: ObBill) {
        FirebaseConfiguration
            .getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(PreferencesConfig(context).getUserAuthId())
            .child(obBill.id!!).setValue(obBill)
    }

    fun insert(obUser: ObUser) {
        FirebaseConfiguration
            .getFirebaseDatabase()
            .child(Consts.FIREBASE_USER)
            .child(obUser.id!!).setValue(obUser)
    }

    fun delete(context: Context,obBill: ObBill) {
        FirebaseConfiguration
            .getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(PreferencesConfig(context).getUserAuthId())
            .child(obBill.id!!).setValue(null)
    }

    fun delete(obUser: ObUser) {
        FirebaseConfiguration
            .getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(obUser.id!!).setValue(null)
    }
}