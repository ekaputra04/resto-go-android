package com.example.restogo

import retrofit2.http.GET

interface ApiService {
    @GET("menu-categories")
    suspend fun getMenuCategories(): MenuCategoryResponse

    @GET("menus")
    suspend fun getMenus(): MenuResponse

    @GET("extra-menus")
    suspend fun getExtraMenus(): ExtraMenuResponse
}
