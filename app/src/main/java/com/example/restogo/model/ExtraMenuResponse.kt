package com.example.restogo.model

import android.os.Parcelable
import com.example.restogo.model.ExtraMenu
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtraMenuResponse(
    val data: List<ExtraMenu>
) : Parcelable
