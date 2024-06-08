package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuCategory(
    var _id: String,
    var name: String
) : Parcelable

