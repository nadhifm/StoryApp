package com.nadhifm.storyapp.presentation.home

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import app.cash.turbine.test
import com.nadhifm.storyapp.domain.model.Story
import com.nadhifm.storyapp.domain.use_case.GetStoriesPagingDataUseCase
import com.nadhifm.storyapp.utils.MainCoroutineRule
import com.nadhifm.storyapp.utils.MyDiffCallback
import com.nadhifm.storyapp.utils.NoopListCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
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
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var getStoriesPagingDataUseCase: GetStoriesPagingDataUseCase

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(getStoriesPagingDataUseCase)
    }

    @Test
    fun `when Get Stories Paging Data Should Return Paging Data`() = runTest {
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
        val pagingData = PagingData.from(listStories)
        val expectedStoriesPagingData = flow {
            emit(pagingData)
        }
        Mockito.`when`(getStoriesPagingDataUseCase()).thenReturn(expectedStoriesPagingData)

        homeViewModel.stories.test {
            homeViewModel.getStories()
            Mockito.verify(getStoriesPagingDataUseCase).invoke()

            val differ = AsyncPagingDataDiffer(
                diffCallback = MyDiffCallback(),
                updateCallback = NoopListCallback(),
                workerDispatcher = mainCoroutineScopeRule.dispatcher
            )
            awaitItem()
            val result = awaitItem()
            differ.submitData(result)
            advanceUntilIdle()

            assertNotNull(result)
            assertEquals(listStories, differ.snapshot().items)
        }
    }
}