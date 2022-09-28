package com.nadhifm.storyapp.data.repository

import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nadhifm.storyapp.data.local.data_store.DataStoreManager
import com.nadhifm.storyapp.data.mapper.toStory
import com.nadhifm.storyapp.data.paging.StoryPagingSource
import com.nadhifm.storyapp.data.remote.network.APIService
import com.nadhifm.storyapp.data.remote.request.LoginRequest
import com.nadhifm.storyapp.data.remote.request.RegisterRequest
import com.nadhifm.storyapp.data.remote.response.ErrorResponse
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Constans.PAGE_SIZE
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val dataStoreManager: DataStoreManager,
    private val storyPagingSource: StoryPagingSource
) : AppRepository {
    override fun register(name: String, email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(RegisterRequest(
                name = name,
                email = email,
                password = password
            ))
            emit(Resource.Success(response.message))
        } catch (e: Exception) {
            when(e) {
                is HttpException -> {
                    val errorMessageResponseType = object : TypeToken<ErrorResponse>() {}.type
                    val error: ErrorResponse = Gson().fromJson(e.response()?.errorBody()?.charStream(), errorMessageResponseType)
                    emit(Resource.Error(error.message))
                }
                else -> {
                    emit(Resource.Error("An unexpected error occurred"))
                }
            }
        }
    }

    override fun login(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(LoginRequest(
                email = email,
                password = password
            ))
            dataStoreManager.saveName(response.loginResultResponse.name)
            dataStoreManager.saveToken(response.loginResultResponse.token)
            emit(Resource.Success(response.message))
        } catch (e: Exception) {
            when(e) {
                is HttpException -> {
                    val errorMessageResponseType = object : TypeToken<ErrorResponse>() {}.type
                    val error: ErrorResponse = Gson().fromJson(e.response()?.errorBody()?.charStream(), errorMessageResponseType)
                    emit(Resource.Error(error.message))
                }
                else -> {
                    emit(Resource.Error("An unexpected error occurred"))
                }
            }
        }
    }

    override fun checkToken(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val token = dataStoreManager.token.first()
            if (token != "") {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Token Not Exist", false))
            }
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred", false))
        }
    }

    override fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            dataStoreManager.saveToken("")
            dataStoreManager.saveName("")
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred", false))
        }
    }

    override fun addStory(photo: File, description: String, location: Location?): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val token = dataStoreManager.token.first()
            if (token != "") {
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val photoRequestBody = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", photo.name, photoRequestBody)
                val response = if (location != null) {
                    val latRequestBody = location.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val longRequestBody = location.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    apiService.addStory(
                        token = "Bearer $token",
                        photo = photoPart,
                        description = descriptionRequestBody,
                        lat = latRequestBody,
                        lon = longRequestBody
                    )
                } else {
                    apiService.addStory(
                        token = "Bearer $token",
                        photo = photoPart,
                        description = descriptionRequestBody
                    )
                }
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error("Token Not Empty"))
            }
        } catch (e: Exception) {
            when(e) {
                is HttpException -> {
                    val errorMessageResponseType = object : TypeToken<ErrorResponse>() {}.type
                    val error: ErrorResponse = Gson().fromJson(e.response()?.errorBody()?.charStream(), errorMessageResponseType)
                    emit(Resource.Error(error.message))
                }
                else -> {
                    emit(Resource.Error("An unexpected error occurred"))
                }
            }
        }
    }

    override fun getName(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val name = dataStoreManager.name.first()
            emit(Resource.Success(name))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred"))
        }
    }

    override fun getStories(isLocationExist: Boolean): Flow<Resource<List<Story>>> = flow {
        emit(Resource.Loading())
        try {
            val token = dataStoreManager.token.first()
            if (token == "") {
                emit(Resource.Error("Token Not Empty"))
            } else {
                val apiResponse =
                    apiService.getStories(
                        token = "Bearer $token",
                        location = if (isLocationExist) 1 else 0,
                    )
                val listStoryResponse = apiResponse.listStoryResponse
                emit(Resource.Success(listStoryResponse.map { it.toStory() }))
            }
        } catch (e: Exception) {
            when(e) {
                is HttpException -> {
                    val errorMessageResponseType = object : TypeToken<ErrorResponse>() {}.type
                    val error: ErrorResponse = Gson().fromJson(e.response()?.errorBody()?.charStream(), errorMessageResponseType)
                    emit(Resource.Error(error.message))
                }
                else -> {
                    emit(Resource.Error("An unexpected error occurred"))
                }
            }
        }
    }

    override fun getStoriesPagingData(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                storyPagingSource
            }
        ).flow.map { pagingData ->
            pagingData.map {
                it.toStory()
            }
        }
    }
}