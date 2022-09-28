package com.nadhifm.storyapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.use_case.GetStoriesPagingDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStoriesPagingDataUseCase: GetStoriesPagingDataUseCase
) : ViewModel() {
    private val _stories = MutableStateFlow<PagingData<Story>>(PagingData.empty())
    val stories = _stories.asStateFlow()

    fun getStories() {
        val result = getStoriesPagingDataUseCase()
        result
            .cachedIn(viewModelScope)
            .onEach {
                _stories.value = it
            }
            .launchIn(viewModelScope)
    }
}