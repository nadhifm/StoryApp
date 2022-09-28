package com.nadhifm.storyapp.domain.repository

import android.location.Location
import androidx.paging.PagingData
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AppRepository {
    fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<String>>
    fun login(
        email: String,
        password: String
    ): Flow<Resource<String>>
    fun checkToken(): Flow<Resource<Boolean>>
    fun logout(): Flow<Resource<Boolean>>
    fun getStories(isLocationExist: Boolean): Flow<Resource<List<Story>>>
    fun getStoriesPagingData(): Flow<PagingData<Story>>
    fun addStory(
        photo: File,
        description: String,
        location: Location?
    ): Flow<Resource<String>>
    fun getName(): Flow<Resource<String>>
}