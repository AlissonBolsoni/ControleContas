package br.com.alisson.billcontrol.data.models

import br.com.alisson.billcontrol.utils.Base64Crypt
import java.io.Serializable

open class ObUser(
    var id: String?,
    var name: String,
    var email: String,
    var password: String
) : Serializable {
    constructor() : this(null, "", "","")
}

fun ObUser.toDto():ObUser{
    val emailBase64 = Base64Crypt.encode(email)
    return ObUser(id, name, emailBase64, password)
}

fun ObUser.toDao():ObUser{
    val emailBase64 = Base64Crypt.decode(email)
    return ObUser(id, name, emailBase64, password)
}