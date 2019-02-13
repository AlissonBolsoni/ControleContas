package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import br.com.alisson.billcontrol.ui.BillAdapter
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.Consts
import br.com.alisson.billcontrol.utils.DateUtils
import br.com.alisson.billcontrol.utils.Formats
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.bill_filter_item.*
import kotlinx.android.synthetic.main.fragment_bill_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class BillsFragment : BaseFragment(), BroadcastInterfaceCallback {

    private lateinit var month: Date
    private lateinit var cal: Calendar
    private lateinit var broadcast: BillBroadcast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bill_layout, container, false)

        title = getString(R.string.title_home)

        super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        broadcast = BillBroadcast.register(mainActivity!!, this, BillBroadcast.ACTION_DATABASE_CHANGE)
        configFilter()
        putOnList()
    }

    override fun onStop() {
        BillBroadcast.unregister(mainActivity!!, broadcast)
        super.onStop()
    }

    override fun putOnList() {
        val key = DateUtils.getCacheKey(month.time)
        val pair = CacheObBils.get(key)
        setTitle(pair.second)
        createAdapter(pair.first)
    }

    private fun configFilter() {
        cal = Calendar.getInstance()
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0)

        bill_filter_left.setOnClickListener { left() }
        bill_filter_right.setOnClickListener { right() }
        setDateToFilter()
    }

    private fun createAdapter(bills: ArrayList<ObBill>?) {
        if (bills != null) {
            val adapter = BillAdapter(mainActivity!!, bills) { obBill ->
                mainActivity!!.moveToFragment(MainActivity.FRAGMENT_ADD, obBill)
            }

            frag_bill_recycler!!.adapter = adapter
            frag_bill_recycler!!.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setDateToFilter() {
        month = cal.time
        bill_filter_date.text = Formats.SDF_MONTH_YEAR.format(month).toUpperCase(Locale.getDefault())
    }

    private fun left() {
        this.cal = DateUtils.manageMonthCalendar(month, DateUtils.MINUS)

        setDateToFilter()
        putOnList()
    }

    private fun right() {
        this.cal = DateUtils.manageMonthCalendar(month, DateUtils.ADD)
        setDateToFilter()
        putOnList()
    }
}