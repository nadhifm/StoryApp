package com.nadhifm.storyapp.domain.use_case

import app.cash.turbine.test
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
class CheckTokenUseCaseTest {

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var checkTokenUseCase: CheckTokenUseCase

    @Before
    fun setUp() {
        checkTokenUseCase = CheckTokenUseCase(appRepository)
    }

    @Test
    fun `when User Already Login Should Return Success and True`() = runTest {
        val expectedCheckTokenResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(true))
        }
        Mockito.`when`(appRepository.checkToken()).thenReturn(expectedCheckTokenResult)

        checkTokenUseCase().test {
            Mockito.verify(appRepository).checkToken()

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
        val expectedCheckTokenResult = flow {
            emit(Resource.Loading())
            emit(Resource.Error("Token Not Exists",false))
        }
        Mockito.`when`(appRepository.checkToken()).thenReturn(expectedCheckTokenResult)

        checkTokenUseCase().test {
            Mockito.verify(appRepository).checkToken()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.data)
            assertEquals(false, result.data)

            awaitComplete()
        }
    }
}