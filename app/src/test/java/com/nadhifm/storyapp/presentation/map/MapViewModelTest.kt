package com.nadhifm.storyapp.presentation.map

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.use_case.GetStoriesUseCase
import com.nadhifm.storyapp.utils.MainCoroutineRule
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var getStoriesUseCase: GetStoriesUseCase

    private lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(getStoriesUseCase)
    }

    @Test
    fun `when Get Stories Success Should Return List Stories`() = runTest {
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
        Mockito.`when`(getStoriesUseCase(true)).thenReturn(expectedStories)

        mapViewModel.stories.test {
            mapViewModel.getStories()
            Mockito.verify(getStoriesUseCase).invoke(true)

            awaitItem()
            var result = awaitItem()
            assertTrue(result.isLoading)

            result = awaitItem()
            assertTrue(!result.isLoading)
            assertNotNull(result.data)
            assertEquals(listStories, result.data)
        }
    }

    @Test
    fun `when Network Error Should Return Error Message`() = runTest {
        val message = "An unexpected error occurred"
        val expectedStories = flow<Resource<List<Story>>> {
            emit(Resource.Loading())
            emit(Resource.Error(message))
        }
        Mockito.`when`(getStoriesUseCase(true)).thenReturn(expectedStories)

        mapViewModel.errorSnackbar.test {
            mapViewModel.getStories()
            Mockito.verify(getStoriesUseCase).invoke(true)

            val errorMessage = awaitItem()
            assertEquals(message, errorMessage)
        }
    }
}