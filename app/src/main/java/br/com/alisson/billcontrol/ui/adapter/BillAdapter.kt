package br.com.alisson.billcontrol.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.alisson.billcontrol.R
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.utils.Formats
import br.com.alisson.billcontrol.utils.MoneyUtil
import java.util.*


class BillAdapter(
    private val context: Context,
    private val bills: List<ObBill>,
    private val onClickItem: (ObBill, Int) -> Unit
) : RecyclerView.Adapter<BillAdapter.ItemHolder>() {

    companion object {
        const val CLICK = 10
        const val COPY = 20
        const val DELETE = 30
        const val MOVE = 40
    }

    class ItemHolder(itemView: View, private val adapter: BillAdapter) :
        RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        val description: TextView = itemView.findViewById(R.id.frag_bill_item_description)
        val date: TextView = itemView.findViewById(R.id.frag_bill_item_date)
        val billValue: TextView = itemView.findViewById(R.id.frag_bill_item_value)
        val payed: ImageView = itemView.findViewById(R.id.frag_bill_item_payed)

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (v != null){
                val obBill = v.tag as ObBill

                val copy = menu!!.add(adapter.context.getString(R.string.copy_next_month))
                copy.setOnMenuItemClickListener {
                    adapter.onClickItem(obBill, COPY)
                    false
                }

                val move = menu.add(adapter.context.getString(R.string.move_next_month))
                move.setOnMenuItemClickListener {
                    adapter.onClickItem(obBill, MOVE)
                    false
                }

                val del = menu.add(adapter.context.getString(R.string.delete))
                del.setOnMenuItemClickListener {
                    adapter.onClickItem(obBill, DELETE)
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemHolder(
            LayoutInflater.from(context).inflate(R.layout.fragment_bill_item_layout, parent, false), this
        )

    override fun getItemCount() = bills.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        bills[position].apply {
            holder.date.text = Formats.SDF.format(Date(this.expirationDate))
            holder.description.text = this.description ?: ""
            holder.billValue.text = MoneyUtil.formatBR(this.billValue)
            holder.payed.visibility = if (this.paymentDate == null) View.INVISIBLE else View.VISIBLE
            holder.itemView.tag = this

            holder.itemView.setOnClickListener {
                onClickItem(it.tag as ObBill, CLICK)
            }
        }


    }

}