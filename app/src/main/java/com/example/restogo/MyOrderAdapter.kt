package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restogo.model.DetailMenu
import com.example.restogo.model.OrderMenuData
import java.text.SimpleDateFormat
import java.util.Locale

class MyOrderAdapter(
    private val menus: List<OrderMenuData>,
    private val onItemClick: (OrderMenuData) -> Unit
) : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMenu: ImageView = itemView.findViewById(R.id.img_item_my_order)
        val tvDate: TextView = itemView.findViewById(R.id.tv_item_my_order_tanggal)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_my_order_nama_menu)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_item_my_order_harga)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_item_my_order_banyak)
        val tvExtraMenu: TextView = itemView.findViewById(R.id.tv_item_my_order_extra_menu)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_item_my_order_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_orders, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menus[position]

        // Mengatur gambar pada ImageView menggunakan Glide
        Glide.with(holder.imgMenu.context)
            .load(menu.menu.url_image)
            .placeholder(R.drawable.logo) // Gambar placeholder sementara menunggu loading
            .error(R.drawable.logo) // Gambar default jika gagal memuat
            .into(holder.imgMenu)

        // Format tanggal dari JSON menjadi dd-MM-yyyy
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        holder.tvDate.text = dateFormat.format(menu.date)

        holder.tvName.text = menu.menu.name
        holder.tvPrice.text = "Rp.${menu.menu.price}"
        holder.tvQuantity.text = "Jumlah: ${menu.quantity}"
        holder.tvExtraMenu.text = menu.extraMenu?.let {
            "Extra: ${it.name} - Rp.${it.price}"
        } ?: "No Extra Menu"
        holder.tvStatus.text = if (menu.isDone) "Selesai" else "Dalam Proses"

        holder.itemView.setOnClickListener {
            onItemClick(menu)
        }
    }

    override fun getItemCount(): Int = menus.size
}
