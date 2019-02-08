package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.ui.BillAdapter
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.utils.DateUtils
import br.com.alisson.billcontrol.utils.Formats
import io.realm.RealmResults
import kotlinx.android.synthetic.main.bill_filter_item.*
import kotlinx.android.synthetic.main.fragment_bill_layout.*
import java.util.*

class BillsFragment : BaseFragment() {

    private lateinit var month: Date
    private lateinit var cal: Calendar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bill_layout, container, false)

        title = getString(R.string.title_home)
        setTitle()

        super.onCreateView(inflater, container, savedInstanceState)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configFilter()

        getBillList()
    }

    private fun configFilter() {
        cal = Calendar.getInstance()
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0)

        bill_filter_left.setOnClickListener { left() }
        bill_filter_right.setOnClickListener { right() }
        setDateToFilter()
    }

    private fun getBillList() {
        val tempCal = cal
        tempCal.set(Calendar.MONTH, cal.get(Calendar.MONTH).plus(1))
        val maxDate = tempCal.time
        val bills = mainActivity!!.getRealmQuery().between("expirationDate",month.time, maxDate.time).findAll()
        createAdapter(bills)
    }

    private fun createAdapter(bills: RealmResults<ObBill>?) {
        if (bills != null){
            val adapter = BillAdapter(activity!!, bills) {
                    obBill ->
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

    private fun left(){
        this.cal = DateUtils.manageMonthCalendar(month, DateUtils.MINUS)

        setDateToFilter()
        getBillList()
    }

    private fun right(){
        this.cal = DateUtils.manageMonthCalendar(month, DateUtils.ADD)
        setDateToFilter()
        getBillList()
    }
}