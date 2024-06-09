package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtraMenuResponse(
    val data: List<ExtraMenu>
) : Parcelable
