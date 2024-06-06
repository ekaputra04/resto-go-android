package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuCategory(
    val _id: String,
    var name: String
) : Parcelable

