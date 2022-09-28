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
class LogoutUseCaseTest {

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        logoutUseCase = LogoutUseCase(appRepository)
    }

    @Test
    fun `when Logout Success Should Return Success and True`() = runTest {
        val expectedLogoutResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(true))
        }
        Mockito.`when`(appRepository.logout()).thenReturn(expectedLogoutResult)

        logoutUseCase().test {
            Mockito.verify(appRepository).logout()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(true, result.data)

            awaitComplete()
        }
    }
}