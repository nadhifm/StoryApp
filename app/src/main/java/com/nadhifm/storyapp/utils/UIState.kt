package com.nadhifm.storyapp.utils

data class UIState<T>(
    val data: T? = null,
    val isLoading: Boolean = false
)