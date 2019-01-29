package br.com.alisson.billcontrol.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.models.ObBill
import br.com.alisson.billcontrol.utils.Formats
import br.com.alisson.billcontrol.utils.MoneyUtil
import java.util.*

class BillAdapter(
    private val context: Context,
    private val bills: List<ObBill>,
    private val onClickItem: (ObBill) -> Unit
) : RecyclerView.Adapter<BillAdapter.Companion.ItemHolder>() {

    companion object {
        class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val description: TextView = itemView.findViewById(R.id.frag_bill_item_description)
            val date: TextView = itemView.findViewById(R.id.frag_bill_item_date)
            val billValue: TextView = itemView.findViewById(R.id.frag_bill_item_value)
            val payed: ImageView = itemView.findViewById(R.id.frag_bill_item_payed)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemHolder(LayoutInflater.from(context).inflate(R.layout.fragment_bill_item_layout, parent, false))

    override fun getItemCount() = bills.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        bills[position].apply {

            holder.date.text = Formats.SDF.format(Date(this.expirationDate))
            holder.description.text = this.description?:""
            holder.billValue.text = MoneyUtil.formatBR(this.billValue)
            holder.payed.visibility = if(this.paymentDate == null) View.INVISIBLE else View.VISIBLE
            holder.itemView.tag = this
            holder.itemView.setOnClickListener {
                onClickItem(it.tag as ObBill)
            }
        }

    }

}