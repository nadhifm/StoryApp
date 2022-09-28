package com.nadhifm.storyapp.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("loginResult")
    val loginResultResponse: LoginResultResponse,
    @SerializedName("message")
    val message: String
)