package br.com.caiorodri.agenpet.api.model

import com.google.gson.annotations.SerializedName

data class PageResponse<T>(

    @SerializedName("content")
    val content: List<T>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Long

)