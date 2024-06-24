package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.Menu

class HomeMenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMenu: ImageView = itemView.findViewById(R.id.img_item_home_menus)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_home_menus_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_item_home_menus_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_menus, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menus[position]

        // Mengubah nama file menjadi resource ID
        val context = holder.imgMenu.context
        val resourceId = context.resources.getIdentifier(menu.url_image.replace(".jpg", ""), "drawable", context.packageName)

        // Mengatur gambar pada ImageView
        if (resourceId != 0) { // Pastikan resource ID valid
            holder.imgMenu.setImageResource(resourceId)
        } else {
            // Anda bisa menetapkan gambar default jika resource ID tidak valid
            holder.imgMenu.setImageResource(R.drawable.logo)
        }

        holder.tvName.text = menu.name
        holder.tvPrice.text = menu.price.toString()

        holder.itemView.setOnClickListener {
            onItemClick(menu)
        }
    }

    override fun getItemCount(): Int = menus.size
}
