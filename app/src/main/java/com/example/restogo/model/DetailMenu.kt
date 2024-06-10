package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMenu(
    var menu: Menu,
    var quantity: Int,
    var extraMenu: ExtraMenu? = null,
    var subTotalMenu: Float
) : Parcelable
