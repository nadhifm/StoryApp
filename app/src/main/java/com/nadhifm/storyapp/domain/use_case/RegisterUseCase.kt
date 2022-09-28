package com.nadhifm.storyapp.domain.use_case

import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = repository.register(name, email, password)
}