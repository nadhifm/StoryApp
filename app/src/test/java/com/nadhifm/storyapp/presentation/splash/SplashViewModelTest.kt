package com.nadhifm.storyapp.presentation.splash

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.use_case.CheckTokenUseCase
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
class SplashViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var checkTokenUseCase: CheckTokenUseCase

    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun setUp() {
        splashViewModel = SplashViewModel(checkTokenUseCase)
    }

    @Test
    fun `when User Already Login Should Return Success and True`() = runTest {
        val expectedCheckTokenResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(true))
        }
        Mockito.`when`(checkTokenUseCase()).thenReturn(expectedCheckTokenResult)

        splashViewModel.checkTokenResult.test {
            splashViewModel.checkToken()
            Mockito.verify(checkTokenUseCase).invoke()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(true, result.data)
        }
    }

    @Test
    fun `when User Not Login Should Return Error and False`() = runTest {
        val expectedCheckTokenResult = flow {
            emit(Resource.Loading())
            emit(Resource.Error("Token Not Exist", false))
        }
        Mockito.`when`(checkTokenUseCase()).thenReturn(expectedCheckTokenResult)

        splashViewModel.checkTokenResult.test {
            splashViewModel.checkToken()
            Mockito.verify(checkTokenUseCase).invoke()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Error)
            assertNotNull(result.data)
            assertEquals(false, result.data)
        }
    }
}