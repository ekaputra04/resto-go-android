package com.example.restogo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val menuList: List<Menu>, private val onItemClick: (Menu) -> Unit) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_menus, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.bind(menu)
    }

    override fun getItemCount() = menuList.size

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_menu_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_item_menu_price)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(menu: Menu) {
            nameTextView.text = menu.name
            priceTextView.text = menu.price.toString()
        }

        override fun onClick(v: View?) {
            onItemClick(menuList[adapterPosition])
        }
    }
}
