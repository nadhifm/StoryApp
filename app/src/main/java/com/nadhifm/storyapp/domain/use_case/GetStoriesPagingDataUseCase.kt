package com.nadhifm.storyapp.domain.use_case

import androidx.paging.PagingData
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoriesPagingDataUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<PagingData<Story>> = repository.getStoriesPagingData()
}