package com.example.restogo

import android.os.Parcelable
import com.example.restogo.model.MenuCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuCategoryResponse(
    val data: List<MenuCategory>
) : Parcelable