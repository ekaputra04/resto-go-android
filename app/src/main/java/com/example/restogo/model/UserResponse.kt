package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    val data: List<User>
) : Parcelable
