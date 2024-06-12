package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.DetailMenu
import com.example.restogo.model.OrderObject

class CartAdapter(
    private val detailMenus: MutableList<DetailMenu>,
    private val updateTotalPrice: () -> Unit
) : RecyclerView.Adapter<CartAdapter.DetailMenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailMenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_menus, parent, false)
        return DetailMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailMenuViewHolder, position: Int) {
        val detailMenu = detailMenus[position]
        holder.bind(detailMenu)
    }

    override fun getItemCount() = detailMenus.size

    inner class DetailMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_item_cart_menus_name)
        private val tvExtraMenu: TextView = itemView.findViewById(R.id.tv_item_cart_menus_tambahan_description)
        private val tvSubTotalMenu: TextView = itemView.findViewById(R.id.tv_item_cart_menus_price)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_item_cart_menus_quantity)
        private val imgPlus: ImageView = itemView.findViewById(R.id.img_item_cart_menus_plus)
        private val imgMinus: ImageView = itemView.findViewById(R.id.img_item_cart_menus_minus)
        private val imgMenu: ImageView = itemView.findViewById(R.id.img_item_cart_menus)

        private lateinit var newDetailMenu: DetailMenu

        @SuppressLint("SetTextI18n", "DiscouragedApi")
        fun bind(oldDetailMenu: DetailMenu) {
            newDetailMenu = oldDetailMenu

            // Mengubah nama file menjadi resource ID
            val context = itemView.context
            val resourceId = context.resources.getIdentifier(newDetailMenu.menu.url_image.replace(".jpg", ""), "drawable", context.packageName)

            // Mengatur gambar pada ImageView
            if (resourceId != 0) { // Pastikan resource ID valid
                imgMenu.setImageResource(resourceId)
            } else {
                // Anda bisa menetapkan gambar default jika resource ID tidak valid
                imgMenu.setImageResource(R.drawable.logo)
            }

            tvName.text = newDetailMenu.menu.name
            tvExtraMenu.text = newDetailMenu.extraMenu?.name ?: ""
            val extraMenuPrice = newDetailMenu.extraMenu?.price ?: 0
            val subTotalMenu = (newDetailMenu.menu.price * newDetailMenu.quantity) + extraMenuPrice
            newDetailMenu.subTotalMenu = subTotalMenu.toFloat()
            tvSubTotalMenu.text = "Rp.$subTotalMenu"
            tvQuantity.text = newDetailMenu.quantity.toString()

            imgPlus.setOnClickListener {
                newDetailMenu.quantity += 1
                updateSubTotal()
                updateTotalPrice()
            }

            imgMinus.setOnClickListener {
                if (newDetailMenu.quantity > 1) {
                    newDetailMenu.quantity -= 1
                    updateSubTotal()
                    updateTotalPrice()
                }
            }

            OrderObject.details -= oldDetailMenu
            OrderObject.details += newDetailMenu
        }

        @SuppressLint("SetTextI18n")
        private fun updateSubTotal() {
            val extraMenuPrice = newDetailMenu.extraMenu?.price ?: 0
            val subTotalMenu = (newDetailMenu.menu.price * newDetailMenu.quantity) + extraMenuPrice
            newDetailMenu.subTotalMenu = subTotalMenu.toFloat()
            tvSubTotalMenu.text = "Rp.$subTotalMenu"
            tvQuantity.text = newDetailMenu.quantity.toString()
        }
    }
}
