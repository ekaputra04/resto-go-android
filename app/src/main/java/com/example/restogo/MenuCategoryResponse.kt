package com.example.restogo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuCategoryResponse(
    val data: List<MenuCategory>
) : Parcelable