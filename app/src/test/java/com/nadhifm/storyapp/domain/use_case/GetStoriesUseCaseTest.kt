package com.nadhifm.storyapp.domain.use_case

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetStoriesUseCaseTest {

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var getStoriesUseCase: GetStoriesUseCase

    @Before
    fun setUp() {
        getStoriesUseCase = GetStoriesUseCase(appRepository)
    }

    @Test
    fun `when Get Stories Success Should Return Success and List Stories`() = runTest {
        val listStories = listOf(
            Story(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            Story(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            Story(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            Story(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
            Story(
                createdAt = "2022-03-31T09:39:27.502Z",
                description = "Jika sudah cinta programming, maka belajar ngoding pun terasa lebih mudah dan enjoy",
                id = "story-FPkiCfHWfRpjwiY0",
                lat = -6.0019502,
                lon = 106.0662807,
                name = "Dicoding",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1648719567500_ssbAAkGs.png"
            ),
        )
        val expectedStories = flow {
            emit(Resource.Loading())
            emit(Resource.Success(listStories))
        }
        Mockito.`when`(appRepository.getStories(false)).thenReturn(expectedStories)

        getStoriesUseCase().test {
            Mockito.verify(appRepository).getStories(false)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(listStories.size, result.data?.size)

            awaitComplete()
        }
    }

    @Test
    fun `when Network Error Should Return Error and Message`() = runTest {
        val message = "An unexpected error occurred"
        val expectedStories = flow<Resource<List<Story>>> {
            emit(Resource.Loading())
            emit(Resource.Error(message))
        }
        Mockito.`when`(appRepository.getStories(false)).thenReturn(expectedStories)

        getStoriesUseCase().test {
            Mockito.verify(appRepository).getStories(false)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.message)
            assertEquals(message, result.message)

            awaitComplete()
        }
    }
}