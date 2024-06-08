package com.example.restogo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HomeMenuAdapter(
    private val menus: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imgMenu: ImageView = itemView.findViewById(R.id.img_item_home_menus)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_home_menus_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_item_home_menus_price)
//        val btnDetail: Button = itemView.findViewById(R.id.btn_item_home_menus_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_menus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menus[position]
//        Glide.with(holder.imgMenu.context).load(menu.url_image).into(holder.imgMenu)
        holder.tvName.text = menu.name
        holder.tvPrice.text = menu.price.toString()

//        holder.btnDetail.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, DetailMenuActivity::class.java)
//            intent.putExtra("menu_id", menu._id)  // Pass any data you need
//            ContextCompat.startActivity(context, intent, null)
//        }

        holder.itemView.setOnClickListener {
            onItemClick(menu)
        }
    }

    override fun getItemCount(): Int = menus.size
}
