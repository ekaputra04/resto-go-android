package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtraMenu(
    val _id: String,
    val name: String,
    val price: Int
) : Parcelable
