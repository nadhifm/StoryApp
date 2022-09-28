package com.nadhifm.storyapp.data.mapper

import com.nadhifm.storyapp.data.remote.response.StoryResponse
import com.nadhifm.storyapp.domain.model.Story

fun StoryResponse.toStory(): Story {
    return Story(
        createdAt = createdAt,
        description = description,
        id = id,
        lon = lon,
        lat = lat,
        name = name,
        photoUrl = photoUrl
    )
}