package com.example.restogo

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgbbApiService {
    @Multipart
    @POST("upload?key=1f02d653792d16d11d56ede99348f6e1")
    suspend fun uploadImage(@Part image: MultipartBody.Part): ImgbbResponse
}
