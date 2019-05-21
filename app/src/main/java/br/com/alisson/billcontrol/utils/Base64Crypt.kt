package br.com.alisson.billcontrol.utils

import android.util.Base64


object Base64Crypt {

    fun encode(text: String): String{
        var encoded = Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)
        encoded = encoded.replace("\n","")
        encoded = encoded.replace("\r","")
        return encoded
    }

    fun decode(base64: String):String{
        return String(Base64.decode(base64, Base64.DEFAULT))
    }

}