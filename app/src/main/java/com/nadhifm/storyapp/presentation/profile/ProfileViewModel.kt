package com.nadhifm.storyapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.use_case.GetNameUseCase
import com.nadhifm.storyapp.domain.use_case.LogoutUseCase
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getNameUseCase: GetNameUseCase
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _logoutResult = MutableSharedFlow<Resource<Boolean>>()
    val logoutResult = _logoutResult.asSharedFlow()

    fun getName() {
        val result = getNameUseCase()
        result.onEach {
            when(it) {
                is Resource.Success -> {
                    it.data?.let { data ->
                        _name.value = data
                    }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        val result = logoutUseCase()
        viewModelScope.launch {
            result.collect {
                _logoutResult.emit(it)
            }
        }
    }
}