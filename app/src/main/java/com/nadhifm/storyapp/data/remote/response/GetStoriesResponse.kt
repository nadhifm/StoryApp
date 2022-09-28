package com.nadhifm.storyapp.data.remote.response


import com.google.gson.annotations.SerializedName

data class GetStoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStoryResponse: List<StoryResponse>,
    @SerializedName("message")
    val message: String
)