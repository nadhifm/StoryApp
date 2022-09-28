package com.nadhifm.storyapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.use_case.CheckTokenUseCase
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkTokenUseCase: CheckTokenUseCase
) : ViewModel() {
    private val _checkTokenResult = MutableSharedFlow<Resource<Boolean>>()
    val checkTokenResult = _checkTokenResult.asSharedFlow()

    fun checkToken() {
        val result = checkTokenUseCase()
        viewModelScope.launch {
            result.collect {
                _checkTokenResult.emit(it)
            }
        }
    }
}