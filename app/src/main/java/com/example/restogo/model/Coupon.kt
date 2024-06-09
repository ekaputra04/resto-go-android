package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coupon(
    var couponCode: String,
    var isActive: Boolean = false,
    var discount: Float
) : Parcelable
