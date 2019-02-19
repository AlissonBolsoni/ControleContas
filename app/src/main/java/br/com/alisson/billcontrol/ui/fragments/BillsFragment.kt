package br.com.alisson.billcontrol.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.dao.FirebaseDao
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import br.com.alisson.billcontrol.ui.BillAdapter
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.DateUtils
import br.com.alisson.billcontrol.utils.Formats
import kotlinx.android.synthetic.main.bill_filter_item.*
import kotlinx.android.synthetic.main.fragment_bill_layout.*
import java.util.*

class BillsFragment : BaseFragment(), BroadcastInterfaceCallback {

    private lateinit var month: Date
    private lateinit var cal: Calendar
    private lateinit var broadcast: BillBroadcast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_bill_layout, container, false)
        registerForContextMenu(view)
        title = getString(R.string.title_home)
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
            val adapter = BillAdapter(mainActivity!!, bills, { obBill ->
                mainActivity!!.moveToFragment(MainActivity.FRAGMENT_ADD, obBill)
            },{ obBill ->
                var dialog: AlertDialog? = null
                val view = layoutInflater.inflate(R.layout.dialog_bill, null)
                view.findViewById<Button>(R.id.dialog_copy).setOnClickListener {
                    val nextMont = DateUtils.manageMonthSameDayCalendar(Date(obBill.expirationDate), DateUtils.ADD)
                    obBill.expirationDate = nextMont.time.time
                    obBill.paymentDate = null
                    obBill.id = UUID.randomUUID().toString()
                    FirebaseDao.insert(mainActivity!!, obBill)
                    dialog!!.dismiss()
                }

                view.findViewById<Button>(R.id.dialog_delete).setOnClickListener {
                    FirebaseDao.delete(mainActivity!!, obBill)
                    dialog!!.dismiss()
                }

                val builder = AlertDialog.Builder(mainActivity!!)
                builder.setView(view)
                dialog = builder.create()
                dialog.show()
            })

            frag_bill_recycler!!.adapter = adapter
            registerForContextMenu(frag_bill_recycler)
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