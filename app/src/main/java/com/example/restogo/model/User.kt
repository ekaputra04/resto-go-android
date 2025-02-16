package com.example.restogo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var _id: String,
    var name: String,
    var telephone: String,
    var isAdmin: Boolean
) : Parcelable
