package br.com.alisson.billcontrol.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.services.broadcasts.BroadcastInterfaceCallback
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.ui.adapter.BillPageAdapter
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.DateUtils
import java.util.*

class BillMasterFragment : Fragment(), BroadcastInterfaceCallback {

    private lateinit var broadcast: BillBroadcast
    private lateinit var adapter: BillPageAdapter
    private lateinit var pageAdapter: ViewPager
    private var masterKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bill_master, container, false)

        broadcast = BillBroadcast.register(activity!!, this, BillBroadcast.ACTION_DATABASE_CHANGE)
        pageAdapter = view.findViewById(R.id.master_viewpager)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cal = DateUtils.manageMonthCalendar((Calendar.getInstance()).time, DateUtils.ADD, 0)
        masterKey = DateUtils.getCacheKey(cal.time.time)
        downloadFinished()

    }

    private fun setTitle() {
        (activity as MainActivity).setTitle(getString(R.string.title_home), CacheObBils.getValue(masterKey!!))
    }

    override fun onStop() {
        BillBroadcast.unregister(context!!, broadcast)
        super.onStop()
    }

    override fun downloadFinished() {
        val keys = CacheObBils.getKeys()
        adapter = BillPageAdapter(keys, childFragmentManager)
        pageAdapter.adapter = adapter

        if (masterKey != null && keys.size > 0)
            pageAdapter.currentItem = adapter.list.indexOf(masterKey!!)

        if (keys.size == 0)
            setTitle()

        pageAdapter.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if (p0 == p2) {
                    masterKey = adapter.list[p0]
                    setTitle()
                }
            }

            override fun onPageSelected(p0: Int) {
                masterKey = adapter.list[p0]
                setTitle()
            }
        })
    }
}
