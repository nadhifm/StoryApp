package com.nadhifm.storyapp.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhifm.storyapp.domain.use_case.LoginUseCase
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _loginResult = MutableSharedFlow<Resource<String>>()
    val loginResult = _loginResult.asSharedFlow()
    fun login(
        email: String,
        password: String
    ) {
        val result = loginUseCase(email, password)
        viewModelScope.launch {
            result.collect {
                _loginResult.emit(it)
            }
        }
    }
}