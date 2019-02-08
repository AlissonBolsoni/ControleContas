package br.com.alisson.billcontrol.services.broadcasts

import br.com.alisson.billcontrol.models.ObBill

interface AlarmBroadcastInterface {

    fun alarmBroadcastCallBack(bills: ArrayList<String>)

}