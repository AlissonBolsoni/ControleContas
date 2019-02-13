package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.ui.activity.MainActivity

open class BaseFragment: Fragment() {

    var title = ""
    var mainActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = activity as MainActivity
    }

    fun setTitle(value: Float? = null){
        mainActivity!!.setTitle(title, value)
    }

}