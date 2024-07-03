package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

class OrderMenuData(
    val menu: Menu,
    val quantity: Int,
    val extraMenu: ExtraMenu?,
    val subTotalMenu: Float,
    val date: String,
    val isDone: Boolean
) : Parcelable