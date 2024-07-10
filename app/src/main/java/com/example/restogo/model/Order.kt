package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    var _id: String?,
    var userId: String,
    var user: User?,
    var coupon: Coupon?,
    var totalPrice: Float,
    var date: Date = Date(),
    var isInCart: Boolean = true,
    var isDone: Boolean = false,
    var details: List<DetailMenu>
) : Parcelable