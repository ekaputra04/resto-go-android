package com.example.restogo

import retrofit2.http.GET

interface ApiService {
    @GET("menu-categories")
    suspend fun getMenuCategories(): MenuCategoryResponse
}
