package br.com.alisson.billcontrol.ui.adapter



import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import br.com.alisson.billcontrol.ui.fragments.BillsFragment

class BillPageAdapter(
    val list: ArrayList<String>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(p0: Int): Fragment {
        return BillsFragment.build(list[p0])
    }

    override fun getCount() = list.size
}