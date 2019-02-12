package br.com.alisson.billcontrol.models

import java.io.Serializable

open class ObBill(
    var id: String?,
    var description:String?,
    var expirationDate: Long,
    var billValue: Float,
    var paymentDate: Long?): Serializable {

    constructor() : this(null, null, 0L, 0.0F, null)


}