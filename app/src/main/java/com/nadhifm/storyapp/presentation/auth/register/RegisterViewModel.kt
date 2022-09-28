package com.nadhifm.storyapp.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.use_case.RegisterUseCase
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _registerResult = MutableSharedFlow<Resource<String>>()
    val registerResult = _registerResult.asSharedFlow()
    fun register(
        name: String,
        email: String,
        password: String
    ) {
        val result = registerUseCase(name, email, password)
        viewModelScope.launch {
            result.collect {
                _registerResult.emit(it)
            }
        }
    }
}