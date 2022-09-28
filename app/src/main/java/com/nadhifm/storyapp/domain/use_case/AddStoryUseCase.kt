package com.nadhifm.storyapp.domain.use_case

import android.location.Location
import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class AddStoryUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(
        photo: File,
        description: String,
        location: Location?
    ): Flow<Resource<String>> = repository.addStory(photo, description, location)
}