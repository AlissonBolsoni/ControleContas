package br.com.alisson.billcontrol.ui.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.ui.fragments.AddBillsFragment
import br.com.alisson.billcontrol.ui.fragments.BillsFragment
import br.com.alisson.billcontrol.ui.fragments.ConfigFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val BILL_VALUE = "BILL_VALUE"
        const val FRAGMENT_HOME = R.id.navigation_home
        const val FRAGMENT_ADD = R.id.navigation_add
        const val FRAGMENT_CONFIG = R.id.navigation_config

        const val ACTION_NOTIFICATION = "br.com.alisson.billcontrol.ui.activity.NOTIFICATION_ACTIVITY"
    }

    private var obBill: ObBill? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                FRAGMENT_ADD-> {
                    showFragment(AddBillsFragment(), false)
                    return@OnNavigationItemSelectedListener true
                }
                FRAGMENT_HOME -> {
                    this.obBill = null
                    showFragment(BillsFragment(), false)
                    return@OnNavigationItemSelectedListener true
                }
                FRAGMENT_CONFIG -> {
                    this.obBill = null
                    showFragment(ConfigFragment(), false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    fun moveToFragment(id: Int, obBill: ObBill? = null) {
        this.obBill = obBill
        navigation.selectedItemId = id
    }

    fun setTitle(textTitle: String) {
        title = textTitle
    }

    fun setTitleAddButton(text: String) {
        val menuItem = navigation.menu.findItem(R.id.navigation_add)
        if (getString(R.string.title_add) == text) {
            menuItem.title = text
            menuItem.setIcon(R.drawable.plus)
        } else {
            menuItem.title = text
            menuItem.setIcon(R.drawable.pencil)
        }
    }

    private fun showFragment(fragment: Fragment, enableRollback: Boolean) {

        if (this.obBill != null) {
            val bundle = Bundle()
            bundle.putSerializable(BILL_VALUE, this.obBill!!)
            fragment.arguments = bundle
        }

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.main_frame_layout, fragment)

        if (enableRollback)
            transaction.addToBackStack(null)

        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        moveToFragment(R.id.navigation_home)

    }

}
