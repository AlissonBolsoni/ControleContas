package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.dao.FirebaseDao
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.ui.adapter.BillAdapter
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.DateUtils
import kotlinx.android.synthetic.main.bill_filter_item.*
import kotlinx.android.synthetic.main.fragment_bill_layout.*
import java.util.*

class BillsFragment : BaseFragment(), BroadcastInterfaceCallback {

    companion object {
        private const val PARAM_KEY = "PARAM_KEY"

        fun build(key: String): BillsFragment{
            val bundle = Bundle()
            bundle.putString(PARAM_KEY, key)
            val frag = BillsFragment()
            frag.arguments = bundle
            return frag
        }
    }

    private lateinit var month: Date
    private lateinit var cal: Calendar
    private lateinit var broadcast: BillBroadcast
    private lateinit var adapter: BillAdapter
    private lateinit var key: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_bill_layout, container, false)

        title = getString(R.string.title_home)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        broadcast = BillBroadcast.register(mainActivity!!, this, BillBroadcast.ACTION_DATABASE_CHANGE)
        registerForContextMenu(frag_bill_recycler)

        key = arguments!!.getString(PARAM_KEY)?:""
        setMonthName()

        downloadFinished()
    }

    private fun setMonthName() {
        DateUtils.getMonthByKey(key, bill_filter_date)
    }

    override fun onStop() {
        BillBroadcast.unregister(mainActivity!!, broadcast)
        super.onStop()
    }

    override fun downloadFinished() {
        val list = CacheObBils.get(key)
        createAdapter(list)
    }

    private fun createAdapter(bills: ArrayList<ObBill>?) {
        if (bills != null) {
            adapter = BillAdapter(mainActivity!!, bills) { obBill, opt ->
                if (opt == BillAdapter.CLICK)
                    mainActivity!!.moveToFragment(MainActivity.FRAGMENT_ADD, obBill)
                else if (opt == BillAdapter.COPY){
                    val nextMont = DateUtils.manageMonthSameDayCalendar(Date(obBill.expirationDate), DateUtils.ADD)
                    obBill.expirationDate = nextMont.time.time
                    obBill.paymentDate = null
                    obBill.id = UUID.randomUUID().toString()
                    FirebaseDao.insert(mainActivity!!, obBill)
                }
                else if (opt == BillAdapter.MOVE){
                    val nextMont = DateUtils.manageMonthSameDayCalendar(Date(obBill.expirationDate), DateUtils.ADD)
                    obBill.expirationDate = nextMont.time.time
                    FirebaseDao.insert(mainActivity!!, obBill)
                }
                else if (opt == BillAdapter.DELETE)
                    FirebaseDao.delete(mainActivity!!, obBill)
            }

            frag_bill_recycler!!.adapter = adapter
            registerForContextMenu(frag_bill_recycler)
            frag_bill_recycler!!.adapter!!.notifyDataSetChanged()
        }
    }


}