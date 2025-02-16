package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CouponResponse(
    val data: List<Coupon>
) : Parcelable