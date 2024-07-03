package com.example.restogo.model

import com.example.restogo.MenuCategoryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("menu-categories")
    suspend fun getMenuCategories(): MenuCategoryResponse

    @GET("menus")
    suspend fun getMenus(): MenuResponse

    @GET("extra-menus")
    suspend fun getExtraMenus(): ExtraMenuResponse

    @GET("coupons")
    suspend fun getCoupons(): CouponResponse

    @GET("orders/menus/{userId}")
    suspend fun getMenusFromUserOrder(@Path("userId") userId: String): OrderMenuResponse

    @GET("orders/history/{userId}")
    suspend fun getMenusFromUserOrderHistory(@Path("userId") userId: String): OrderMenuResponse
}
