package com.example.restogo.model

import com.example.restogo.MenuCategoryResponse
import retrofit2.http.GET

interface ApiService {
    @GET("menu-categories")
    suspend fun getMenuCategories(): MenuCategoryResponse

    @GET("menus")
    suspend fun getMenus(): MenuResponse

    @GET("extra-menus")
    suspend fun getExtraMenus(): ExtraMenuResponse

    @GET("coupons")
    suspend fun getCoupons(): CouponResponse
}
