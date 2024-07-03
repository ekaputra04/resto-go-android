package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize

class OrderMenuData(
    val menu: Menu,
    val quantity: Int,
    val extraMenu: ExtraMenu?,
    val subTotalMenu: Float,
    val date: Date,
    val isDone: Boolean
) : Parcelable