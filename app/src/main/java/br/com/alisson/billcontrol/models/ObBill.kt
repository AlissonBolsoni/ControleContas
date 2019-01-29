package br.com.alisson.billcontrol.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.io.Serializable

open class ObBill: RealmObject() {

    @Required
    @PrimaryKey
    var id: String? = null

    @Required
    var description:String? = null

    var expirationDate: Long = 0L

    var billValue: Float = 0.0F

    var paymentDate: Long? = null
}