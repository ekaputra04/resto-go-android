package com.example.restogo.model

import java.util.Date

object OrderObject {
    var userId: String = ""
    var coupon: Coupon? = null
    var totalPrice: Float = 0.0f
    var date: Date = Date()
    var isInCart: Boolean = true
    var isDone: Boolean = false
    var details: List<DetailMenu> = mutableListOf()
}