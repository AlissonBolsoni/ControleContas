package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.ui.BillAdapter
import br.com.alisson.billcontrol.ui.activity.MainActivity
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

class BillsFragment : BaseFragment() {

    private lateinit var month: Date
    private lateinit var cal: Calendar
    private val eventBus: EventBus = EventBus.getDefault()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bill_layout, container, false)

        title = getString(R.string.title_home)
        setTitle()

        super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventBus.register(this)

        configFilter()

        getBillList()
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    @Subscribe
    fun putOnList(bills: ArrayList<ObBill>){
        createAdapter(bills)
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

        val reference = FirebaseConfiguration.getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(PreferencesConfig(mainActivity!!).getUserAuthId())

        reference.orderByChild("expirationDate")
            .startAt(month.time.toDouble())
            .endAt(maxDate.time.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val bills = ArrayList<ObBill>()
                    for (data in p0.children) {
                        val bill = data.getValue(ObBill::class.java)
                        if (bill != null)
                            bills.add(bill)

                    }
                    createAdapter(bills)
                }
            })

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
        getBillList()
    }

    private fun right() {
        this.cal = DateUtils.manageMonthCalendar(month, DateUtils.ADD)
        setDateToFilter()
        getBillList()
    }
}