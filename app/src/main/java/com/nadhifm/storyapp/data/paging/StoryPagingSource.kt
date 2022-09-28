package com.nadhifm.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nadhifm.storyapp.data.local.data_store.DataStoreManager
import com.nadhifm.storyapp.data.remote.network.APIService
import com.nadhifm.storyapp.data.remote.response.ErrorResponse
import com.nadhifm.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class StoryPagingSource(
    private val apiService: APIService,
    private val dataStoreManager: DataStoreManager
) : PagingSource<Int, StoryResponse>() {
    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        val pageIndex = params.key ?: 1
        return try {
            val token = dataStoreManager.token.first()
                if (token == "") {
                    return LoadResult.Error(Exception("Token Not Empty"))
                } else {
                    val apiResponse =
                        apiService.getStories(
                            token = "Bearer $token",
                            page = pageIndex
                        )
                    val listStoryResponse = apiResponse.listStoryResponse
                    LoadResult.Page(
                        data = listStoryResponse,
                        prevKey = if (pageIndex == 1) null else pageIndex,
                        nextKey = if (listStoryResponse.isEmpty()) null else pageIndex + 1
                    )
                }
        } catch (e: Exception) {
            when(e) {
                is HttpException -> {
                    val errorMessageResponseType = object : TypeToken<ErrorResponse>() {}.type
                    val error: ErrorResponse = Gson().fromJson(e.response()?.errorBody()?.charStream(), errorMessageResponseType)
                    return LoadResult.Error(Exception(error.message))
                }
                else -> {
                    return LoadResult.Error(Exception("An unexpected error occurred"))
                }
            }
        }
    }
}