package br.com.alisson.billcontrol.data.tasks

import android.os.AsyncTask
import br.com.alisson.billcontrol.MyApplication

class GetBillsAsync: AsyncTask<Void, Void, Void>(){


    override fun doInBackground(vararg params: Void?): Void? {

        Thread.sleep(2_000)
        MyApplication.singleton.getBillOnFirebase()

        return null
    }


}