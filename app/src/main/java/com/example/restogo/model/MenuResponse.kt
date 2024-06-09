package com.example.restogo.model

import android.os.Parcelable
import com.example.restogo.model.Menu
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuResponse(
    val data: List<Menu>
) : Parcelable
