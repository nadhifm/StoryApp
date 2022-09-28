package com.nadhifm.storyapp.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.use_case.GetStoriesUseCase
import com.nadhifm.storyapp.utils.Resource
import com.nadhifm.storyapp.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase
) : ViewModel() {
    private val _stories = MutableStateFlow<UIState<List<Story>>>(UIState())
    val stories = _stories.asStateFlow()

    private val _errorSnackbar = MutableSharedFlow<String>()
    val errorSnackbar = _errorSnackbar.asSharedFlow()

    fun getStories() {
        val result = getStoriesUseCase(true)
        result.onEach {
            when(it) {
                is Resource.Loading -> {
                    _stories.value = stories.value.copy(
                        data = it.data,
                        isLoading = true
                    )
                }
                is Resource.Success -> {
                    _stories.value = stories.value.copy(
                        data = it.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _stories.value = stories.value.copy(
                        data = it.data,
                        isLoading = false
                    )
                    _errorSnackbar.emit(it.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }
}