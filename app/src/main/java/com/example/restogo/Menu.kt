package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val _id: String,
    val name: String,
    val price: Int,
    val category: String,
    val url_image: String
) : Parcelable
