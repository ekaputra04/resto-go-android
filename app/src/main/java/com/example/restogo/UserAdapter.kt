package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.Menu
import com.example.restogo.model.User

class UserAdapter(private val menuList: List<User>, private val onItemClick: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_menus, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val menu = menuList[position]
        holder.bind(menu)
    }

    override fun getItemCount() = menuList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_menu_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_item_menu_price)

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            nameTextView.text = user.name
            if (user.isAdmin) {
                priceTextView.text = "Admin"
                priceTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.teal_700
                    )
                )
            } else {
                priceTextView.text = "Pelanggan"
                priceTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.primary
                    )
                )
            }
        }

        override fun onClick(v: View?) {
            onItemClick(menuList[adapterPosition])
        }
    }
}
