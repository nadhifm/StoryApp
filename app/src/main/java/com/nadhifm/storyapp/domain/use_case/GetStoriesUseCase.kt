package com.nadhifm.storyapp.domain.use_case

import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoriesUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(isLocationExist: Boolean = false): Flow<Resource<List<Story>>> = repository.getStories(isLocationExist)
}