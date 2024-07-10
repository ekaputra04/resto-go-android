package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.Order

class AllOrderAdapter(
    private val orderList: List<Order>,
    private val onItemClick: (Order) -> Unit
) :
    RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_orders, parent, false)
        return AllOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        val menu = orderList[position]
        holder.bind(menu)
    }

    override fun getItemCount() = orderList.size

    inner class AllOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_item_all_order_tanggal)
        private val menusTextView: TextView =
            itemView.findViewById(R.id.tv_item_all_order_daftar_menu)
        private val usernameTextView: TextView =
            itemView.findViewById(R.id.tv_item_all_order_nama_pelanggan)
        private val totalTextView: TextView = itemView.findViewById(R.id.tv_item_all_order_total)
        private val statusTextView: TextView = itemView.findViewById(R.id.tv_item_all_order_status)

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            val stringBuilder = StringBuilder()
            order.details.forEach { detail ->
                stringBuilder.append("${detail.menu.name} (${detail.quantity})")
                detail.extraMenu?.let { extra ->
                    stringBuilder.append(" - ${extra.name}")
                }
                stringBuilder.append("\n)")
            }

            dateTextView.text = order.date.toString()
            menusTextView.text = stringBuilder.toString()
            usernameTextView.text = order.user?.name
            totalTextView.text = "Rp.${order.totalPrice}"

            if (order.isDone) {
                statusTextView.text = "Selesai"
                statusTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.teal_700
                    )
                )
            } else {
                statusTextView.text = "Belum Selesai"
                statusTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.primary
                    )
                )
            }
        }

        override fun onClick(v: View?) {
            onItemClick(orderList[adapterPosition])
        }
    }
}
