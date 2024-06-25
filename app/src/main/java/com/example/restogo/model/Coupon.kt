package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Coupon(
    var _id: String,
    var couponCode: String,
//    var isActive: Boolean = false,
    var discount: Float,
    var dateStarted: Date,
    var dateEnded: Date
) : Parcelable
