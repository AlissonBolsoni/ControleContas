package br.com.alisson.billcontrol.data.tasks

import android.os.AsyncTask
import br.com.alisson.billcontrol.MyApplication
import br.com.alisson.billcontrol.eventbus.EventLoad
import de.greenrobot.event.EventBus

class GetBillsAsync: AsyncTask<Void, Void, Void>(){


    override fun doInBackground(vararg params: Void?): Void? {

        MyApplication.singleton.getBillOnFirebase()
        Thread.sleep(2_000)
        EventBus.getDefault().post(EventLoad())

        return null
    }


}