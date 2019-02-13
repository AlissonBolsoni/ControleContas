package br.com.alisson.billcontrol.services.broadcasts

import br.com.alisson.billcontrol.models.ObBill

interface BroadcastInterfaceCallback {

    fun alarmBroadcastCallBack(bills: ArrayList<ObBill>){}

    fun putOnList(bills: ArrayList<ObBill>){}

}