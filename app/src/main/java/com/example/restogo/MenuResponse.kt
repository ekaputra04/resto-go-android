package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuResponse(
    val data: List<Menu>
) : Parcelable
