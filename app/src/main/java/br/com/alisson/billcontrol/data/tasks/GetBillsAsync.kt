package br.com.alisson.billcontrol.data.tasks

import android.os.AsyncTask
import br.com.alisson.billcontrol.MyApplication
import br.com.alisson.billcontrol.data.models.ObUser
import java.io.Serializable

class GetBillsAsync(private val operation: Int, private val callback: (ObUser) -> Unit) :
    AsyncTask<Serializable, Void, Void>() {

    companion object {
        const val REGISTER = 10
        const val LOGIN = 20
    }

    override fun doInBackground(vararg params: Serializable?): Void? {
        val auth = MyApplication.singleton.firebaseAuth()

        if (operation == REGISTER){
            val obUser = params[0] as ObUser

            Thread.sleep(2_000)
            MyApplication.singleton.createUserOnFirebase(auth, obUser){
                if (it){
                    MyApplication.singleton.getBillOnFirebase()
                }else{
                    callback(obUser)
                }
            }
        }else if (operation == LOGIN){
            val email = params[0] as String
            val password = params[1] as String

            Thread.sleep(2_000)
            MyApplication.singleton.connectToFirebase(auth, email, password) {
                if (it == null) {
                    MyApplication.singleton.getBillOnFirebase()
                } else {
                    callback(it)
                }
            }
        }

        return null
    }


}