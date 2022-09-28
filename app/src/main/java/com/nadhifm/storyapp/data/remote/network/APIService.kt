package com.nadhifm.storyapp.data.remote.network

import com.nadhifm.storyapp.data.remote.request.LoginRequest
import com.nadhifm.storyapp.data.remote.request.RegisterRequest
import com.nadhifm.storyapp.data.remote.response.AddStoryResponse
import com.nadhifm.storyapp.data.remote.response.GetStoriesResponse
import com.nadhifm.storyapp.data.remote.response.LoginResponse
import com.nadhifm.storyapp.data.remote.response.RegisterResponse
import com.nadhifm.storyapp.utils.Constans.PAGE_SIZE
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface APIService {
    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("location") location: Int = 0,
        @Query("size") size: Int = PAGE_SIZE,
    ): GetStoriesResponse

    @POST("stories")
    @Multipart
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ) : AddStoryResponse
}