package com.example.restogo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.Coupon

class CouponAdapter(
    private val couponsList: List<Coupon>,
    private val onItemClick: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_coupons, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = couponsList[position]
        holder.bind(coupon)
    }

    override fun getItemCount() = couponsList.size

    inner class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val tvCouponCode: TextView = itemView.findViewById(R.id.tv_item_coupons_name)
        private val tvDiscount: TextView = itemView.findViewById(R.id.tv_item_coupons_discount)
        private val tvBerlaku: TextView = itemView.findViewById(R.id.tv_item_coupons_berlaku)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_item_coupons_date)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(coupon: Coupon) {
            tvCouponCode.text = coupon.couponCode
            tvDiscount.text = coupon.discount.toString()

            if (true) {
                tvBerlaku.text = "Berlaku"
                tvBerlaku.setTextColor(ContextCompat.getColor(itemView.context, R.color.teal_700))
            } else {
                tvBerlaku.text = "Tidak Berlaku"
                tvBerlaku.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }

            tvDate.text = "${coupon.dateStarted} - ${coupon.dateEnded}"
        }

        override fun onClick(v: View?) {
            onItemClick(couponsList[adapterPosition])
        }
    }
}
