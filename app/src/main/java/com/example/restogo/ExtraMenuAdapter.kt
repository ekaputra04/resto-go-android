package com.example.restogo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.ExtraMenu

class ExtraMenuAdapter(
    private val extraMenuList: List<ExtraMenu>,
    private val onItemClick: (ExtraMenu) -> Unit
) : RecyclerView.Adapter<ExtraMenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_menus, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val extraMenu = extraMenuList[position]
        holder.bind(extraMenu)
    }

    override fun getItemCount() = extraMenuList.size

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_menu_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_item_menu_price)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(extraMenu: ExtraMenu) {
            nameTextView.text = extraMenu.name
            priceTextView.text = extraMenu.price.toString()
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(extraMenuList[position])
            }
        }
    }
}
