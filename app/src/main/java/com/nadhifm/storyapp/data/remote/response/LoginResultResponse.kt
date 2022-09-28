package com.nadhifm.storyapp.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginResultResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("userId")
    val userId: String
)