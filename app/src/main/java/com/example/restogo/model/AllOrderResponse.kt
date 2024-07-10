package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllOrderResponse(
    val data: List<Order>
) : Parcelable