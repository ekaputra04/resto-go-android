package com.example.restogo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HomeMenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_item_home_menu_categories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_menu_categories, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = menus[position]
        holder.nameTextView.text = category.name

        // Ubah background dan warna teks berdasarkan posisi yang dipilih
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.background_dark)
            holder.nameTextView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.white
                )
            )
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_stroke_dark)
            holder.nameTextView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.dark
                )
            )
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Perbarui item sebelumnya dan item yang baru dipilih
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = menus.size
}