package com.nadhifm.storyapp.presentation.add_story

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.use_case.AddStoryUseCase
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val addStoryUseCase: AddStoryUseCase
) : ViewModel() {
    private val _addStoryResult = MutableSharedFlow<Resource<String>>()
    val addStoryResult = _addStoryResult.asSharedFlow()

    private val _photo = MutableStateFlow<File?>(null)
    val photo = _photo.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)
    private val location = _location.asStateFlow()

    fun setPhoto(file: File? = null) {
        _photo.value = file
    }

    fun setLocation(location: Location? = null) {
        _location.value = location
    }

    fun addStory(description: String) {
        _photo.value?.let { file ->
            val result = addStoryUseCase(file, description, location.value)
            viewModelScope.launch {
                result.collect {
                    _addStoryResult.emit(it)
                }
            }
        }
    }
}