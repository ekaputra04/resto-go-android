package com.example.restogo

import com.google.gson.annotations.SerializedName

data class ImgbbResponse(
    @SerializedName("data") val data: ImgbbData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImgbbData(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url_viewer") val urlViewer: String,
    @SerializedName("url") val url: String,
    @SerializedName("display_url") val displayUrl: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("time") val time: Long,
    @SerializedName("expiration") val expiration: Int,
    @SerializedName("image") val image: ImgbbImage,
    @SerializedName("thumb") val thumb: ImgbbThumbnail,
    @SerializedName("medium") val medium: ImgbbMedium,
    @SerializedName("delete_url") val deleteUrl: String
)

data class ImgbbImage(
    @SerializedName("filename") val filename: String,
    @SerializedName("name") val name: String,
    @SerializedName("mime") val mime: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("url") val url: String
)

data class ImgbbThumbnail(
    @SerializedName("filename") val filename: String,
    @SerializedName("name") val name: String,
    @SerializedName("mime") val mime: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("url") val url: String
)

data class ImgbbMedium(
    @SerializedName("filename") val filename: String,
    @SerializedName("name") val name: String,
    @SerializedName("mime") val mime: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("url") val url: String
)
