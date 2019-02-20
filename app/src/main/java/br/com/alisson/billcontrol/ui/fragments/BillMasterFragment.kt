package br.com.alisson.billcontrol.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.ui.adapter.BillPageAdapter
import br.com.alisson.billcontrol.utils.CacheObBils

class BillMasterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bill_master, container, false)

        val pageAdater = view.findViewById<ViewPager>(R.id.master_viewpager)
        val adapter = BillPageAdapter(CacheObBils.getKeys(), childFragmentManager)
        pageAdater.adapter = adapter
        pageAdater.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if (p0 == p2){
                    setTitle(adapter, p0)
                }
            }

            override fun onPageSelected(p0: Int) = setTitle(adapter, p0)
        })

        return view
    }

    private fun setTitle(adapter: BillPageAdapter, p0: Int) {
        val key = adapter.list[p0]
        (activity as MainActivity).setTitle(getString(R.string.title_home), CacheObBils.getValue(key))
    }


}
