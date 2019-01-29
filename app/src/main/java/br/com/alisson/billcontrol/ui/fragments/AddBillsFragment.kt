package br.com.alisson.billcontrol.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.ui.activity.MainActivity
import br.com.alisson.billcontrol.utils.Formats
import br.com.alisson.billcontrol.utils.SetCalendarToView
import kotlinx.android.synthetic.main.fragment_add_bill_layout.*
import java.util.*

class AddBillsFragment : BaseFragment() {

    private var id: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_bill_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCalendarInFields()
        setUpComponents()
    }

    override fun onPause() {
        super.onPause()

        setFields(null)

        mainActivity!!.setTitleAddButton(getString(R.string.title_add))
    }

    private fun setCalendarInFields() {
        SetCalendarToView.setCalendar(context!!, frag_add_bill_expiration_edit)
        SetCalendarToView.setCalendar(context!!, frag_add_bill_payment_edit)
    }

    private fun setUpComponents() {
        val bundle = arguments
        if (bundle == null) {
            title = getString(R.string.title_add)
        }
        else {
            title = getString(R.string.title_edit)
            mainActivity!!.setTitleAddButton(getString(R.string.title_edit))

            id = bundle.getString(MainActivity.BILL_ID_VALUE)
            val obBill = mainActivity!!.getRealmQuery().equalTo("id", id).findFirst()

            if (obBill != null)
                setFields(obBill)
        }
        setTitle()
    }

    private fun setFields(obBill: ObBill?) {
        if (obBill != null) {
            frag_add_bill_description_edit.setText(obBill.description ?: "")
            frag_add_bill_value_edit.setText(obBill.billValue.toString())
            frag_add_bill_expiration_edit.text = Formats.SDF.format(Date(obBill.expirationDate))

            val payment = if (obBill.paymentDate == null) "" else Formats.SDF.format(Date(obBill.paymentDate!!))
            frag_add_bill_payment_edit.text = payment
        }else{
            frag_add_bill_description_edit.setText("")
            frag_add_bill_value_edit.setText("")
            frag_add_bill_expiration_edit.text = ""
            frag_add_bill_payment_edit.text = ""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_crud_bill, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_crud_bill_save) {
            val obBill = getObBill() ?: return false

            mainActivity!!.getRealm().executeTransaction {
                it.insertOrUpdate(obBill)
            }
            mainActivity!!.moveToFragment(MainActivity.FRAGMENT_HOME)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getObBill(): ObBill? {
        val obBill = ObBill()

        if (id == null)
            id = UUID.randomUUID().toString()

        obBill.id = id
        obBill.description = frag_add_bill_description_edit.text.toString()
        obBill.billValue = frag_add_bill_value_edit.text.toString().toFloat()

        val expiration = frag_add_bill_expiration_edit.text.toString()
        if (expiration == "") {
            Toast.makeText(context!!, getString(R.string.expiration_date_not_empty), Toast.LENGTH_SHORT).show()
            return null
        } else
            obBill.expirationDate = Formats.SDF.parse(expiration).time

        val payment = frag_add_bill_payment_edit.text.toString()
        if (payment == "")
            obBill.paymentDate = null
        else
            obBill.paymentDate = Formats.SDF.parse(payment).time
        return obBill
    }
}