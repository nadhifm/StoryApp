package com.nadhifm.storyapp.data.repository

import app.cash.turbine.test
import com.google.gson.Gson
import com.nadhifm.storyapp.data.local.data_store.DataStoreManager
import com.nadhifm.storyapp.data.paging.StoryPagingSource
import com.nadhifm.storyapp.data.remote.network.APIService
import com.nadhifm.storyapp.data.remote.request.LoginRequest
import com.nadhifm.storyapp.data.remote.request.RegisterRequest
import com.nadhifm.storyapp.data.remote.response.*
import com.nadhifm.storyapp.utils.MainCoroutineRule
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AppRepositoryImplTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var apiService: APIService

    @Mock
    private lateinit var dataStoreManager: DataStoreManager

    private lateinit var appRepositoryImpl: AppRepositoryImpl

    @Mock
    private lateinit var storyPagingSource: StoryPagingSource

    @Before
    fun setUp() {
        appRepositoryImpl = AppRepositoryImpl(apiService, dataStoreManager, storyPagingSource)
    }

    @Test
    fun `when Login Success Should Return Success and Data Not Null`() = runTest {
        val loginRequest = LoginRequest("user@example.com", "123456")
        val loginResultResponse = LoginResultResponse(
            name = "user",
            token = "qwerty",
            userId = "user-zxcv"
        )
        val expectedLoginResponse = LoginResponse(
            error = false,
            loginResultResponse = loginResultResponse,
            message = "Success"
        )
        Mockito.`when`(apiService.login(loginRequest)).thenReturn(expectedLoginResponse)

        appRepositoryImpl.login(
            loginRequest.email,
            loginRequest.password
        ).test {
            Mockito.verify(apiService).login(loginRequest)
            Mockito.verify(dataStoreManager).saveToken(loginResultResponse.token)
            Mockito.verify(dataStoreManager).saveName(loginResultResponse.name)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(expectedLoginResponse.message, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Login Fail Should Return Error and Message Not Null`() = runTest {
        val errorResponse = ErrorResponse(true, "User not found")
        val errorResponseJson = Gson().toJson(errorResponse)
        val errorResponseBody = errorResponseJson.toResponseBody("text/plain".toMediaTypeOrNull())
        val loginRequest = LoginRequest("user@example.com", "123456")
        Mockito.`when`(apiService.login(loginRequest))
            .then {
                throw HttpException(Response.error<ResponseBody>(401, errorResponseBody))
            }

        appRepositoryImpl.login(
            loginRequest.email,
            loginRequest.password
        ).test {
            Mockito.verify(apiService).login(loginRequest)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.message)
            assertEquals(errorResponse.message, result.message)

            awaitComplete()
        }
    }

    @Test
    fun `when Register Success Should Return Success and Data Not Null`() = runTest {
        val registerRequest = RegisterRequest("user","user@example.com", "123456")
        val expectedRegisterResponse = RegisterResponse(
            error = false,
            message = "User created"
        )
        Mockito.`when`(apiService.register(registerRequest)).thenReturn(expectedRegisterResponse)

        appRepositoryImpl.register(
            registerRequest.name,
            registerRequest.email,
            registerRequest.password
        ).test {
            Mockito.verify(apiService).register(registerRequest)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(expectedRegisterResponse.message, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Register Fail Should Return Error and Message Not Null`() = runTest {
        val errorResponse = ErrorResponse(true, "Email is already taken")
        val errorResponseJson = Gson().toJson(errorResponse)
        val errorResponseBody = errorResponseJson.toResponseBody("text/plain".toMediaTypeOrNull())
        val registerRequest = RegisterRequest("user","user@example.com", "123456")
        Mockito.`when`(apiService.register(registerRequest))
            .then {
                throw HttpException(Response.error<ResponseBody>(400, errorResponseBody))
            }

        appRepositoryImpl.register(
            registerRequest.name,
            registerRequest.email,
            registerRequest.password
        ).test {
            Mockito.verify(apiService).register(registerRequest)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.message)
            assertEquals(errorResponse.message, result.message)

            awaitComplete()
        }
    }

    @Test
    fun `when User Already Login Should Return Success and True`() = runTest {
        val expectedToken = flow {
            emit("qwerty")
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.checkToken().test {
            Mockito.verify(dataStoreManager).token

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(true, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when User Not Login Should Return Error and False`() = runTest {
        val expectedToken = flow {
            emit("")
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.checkToken().test {
            Mockito.verify(dataStoreManager).token

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.data)
            assertEquals(false, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when User Already Login Should Return Success and Name`() = runTest {
        val name = "user"
        val expectedName = flow {
            emit(name)
        }
        Mockito.`when`(dataStoreManager.name).thenReturn(expectedName)

        appRepositoryImpl.getName().test {
            Mockito.verify(dataStoreManager).name

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(name, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Logout Success Should Return Success and True`() = runTest {
        appRepositoryImpl.logout().test {
            Mockito.verify(dataStoreManager).saveToken("")
            Mockito.verify(dataStoreManager).saveName("")

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(true, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Add Story Success Should Return Success and Data Not Null`() = runTest {
        val message = "Story created successfully"
        val token = "qwerty"
        val file = File("photo")
        val description = "description"
        val expectedAddStoryResponse = AddStoryResponse(
            error = false,
            message = message
        )
        Mockito.`when`(apiService.addStory(
            token = eq("Bearer $token"),
            photo = any(),
            description = any(),
            lat = eq(null),
            lon = eq(null)
        )).thenReturn(expectedAddStoryResponse)
        val expectedToken = flow {
            emit(token)
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.addStory(file, description, null).test {
            Mockito.verify(dataStoreManager).token
            Mockito.verify(apiService).addStory(
                token = eq("Bearer $token"),
                photo = any(),
                description = any(),
                lat = eq(null),
                lon = eq(null)
            )

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(expectedAddStoryResponse.message, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Add Story Fail Should Return Error and Message Not Null`() = runTest {
        val errorResponse = ErrorResponse(
            true,
            "Payload content length greater than maximum allowed: 1000000"
        )
        val errorResponseJson = Gson().toJson(errorResponse)
        val errorResponseBody = errorResponseJson.toResponseBody("text/plain".toMediaTypeOrNull())
        val token = "qwerty"
        val file = File("photo")
        val description = "description"
        Mockito.`when`(apiService.addStory(
            token = eq("Bearer $token"),
            photo = any(),
            description = any(),
            lat = eq(null),
            lon = eq(null)
        )).then {
            throw HttpException(Response.error<ResponseBody>(413, errorResponseBody))
        }
        val expectedToken = flow {
            emit(token)
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.addStory(file, description, null).test {
            Mockito.verify(dataStoreManager).token
            Mockito.verify(apiService).addStory(
                token = eq("Bearer $token"),
                photo = any(),
                description = any(),
                lat = eq(null),
                lon = eq(null)
            )

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.message)
            assertEquals(errorResponse.message, result.message)

            awaitComplete()
        }
    }

    @Test
    fun `when Get Stories Success Should Return Success and List Stories`() = runTest {
        val token = "qwerty"
        val listStoryResponse = listOf(
            StoryResponse(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            StoryResponse(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            StoryResponse(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            StoryResponse(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            StoryResponse(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
        )
        val expectedGetStoriesResponse = GetStoriesResponse(
            error = false,
            listStoryResponse = listStoryResponse,
            message = "Stories fetched successfully"
        )
        Mockito.`when`(apiService.getStories(
            token = "Bearer $token"
        )).thenReturn(expectedGetStoriesResponse)
        val expectedToken = flow {
            emit(token)
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.getStories(false).test {
            Mockito.verify(dataStoreManager).token
            Mockito.verify(apiService).getStories(token = "Bearer $token")

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(listStoryResponse.size, result.data?.size)

            awaitComplete()
        }
    }

    @Test
    fun `when Network Error Should Return Error and Message`() = runTest {
        val message = "An unexpected error occurred"
        val token = "qwerty"

        Mockito.`when`(apiService.getStories(
            token = "Bearer $token"
        )).then {
            throw Exception(message)
        }
        val expectedToken = flow {
            emit(token)
        }
        Mockito.`when`(dataStoreManager.token).thenReturn(expectedToken)

        appRepositoryImpl.getStories(false).test {
            Mockito.verify(dataStoreManager).token
            Mockito.verify(apiService).getStories(token = "Bearer $token")

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.message)
            assertEquals(message, result.message)

            awaitComplete()
        }
    }

    @Test
    fun `when Get Stories Paging Data Should Return Paging Data`() = runTest {
        appRepositoryImpl.getStoriesPagingData().test {
            val result = awaitItem()
            assertNotNull(result)
        }
    }
}