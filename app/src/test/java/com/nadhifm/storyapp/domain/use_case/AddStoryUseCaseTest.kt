package com.nadhifm.storyapp.domain.use_case

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.repository.AppRepository
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AddStoryUseCaseTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var addStoryUseCase: AddStoryUseCase

    @Before
    fun setUp() {
        addStoryUseCase = AddStoryUseCase(appRepository)
    }

    @Test
    fun `when Add Story Success Should Return Success and Data Not Null`() = runTest {
        val message = "Story created successfully"
        val file = File("photo")
        val description = "Description"
        val expectedAddStoryResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(message))
        }
        Mockito.`when`(appRepository.addStory(file, description, null)).thenReturn(expectedAddStoryResult)

        addStoryUseCase.invoke(file, description, null).test {
            Mockito.verify(appRepository).addStory(file, description, null)

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(message, result.data)

            awaitComplete()
        }
    }

    @Test
    fun `when Add Story Fail Should Return Error and Message Not Null`() = runTest {
        val message = "Payload content length greater than maximum allowed: 1000000"
        val file = File("photo")
        val description = "Description"
        val expectedAddStoryResult = flow<Resource<String>> {
            emit(Resource.Loading())
            emit(Resource.Error(message))
        }
        Mockito.`when`(appRepository.addStory(file, description, null)).thenReturn(expectedAddStoryResult)

        addStoryUseCase.invoke(file, description, null).test {
            Mockito.verify(appRepository).addStory(file, description, null)

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