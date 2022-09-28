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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(appRepository)
    }

    @Test
    fun `when Login Success Should Return Success and Data Not Null`() = runTest {
        val message = "Success"
        val email = "user@example.com"
        val password = "123456"
        val expectedLoginResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(message))
        }
        Mockito.`when`(appRepository.login(email, password)).thenReturn(expectedLoginResult)

        loginUseCase.invoke(email, password).test {
            Mockito.verify(appRepository).login(email, password)

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
    fun `when Login Fail Should Return Error and Message Not Null`() = runTest {
        val message = "User not found"
        val email = "user@example.com"
        val password = "123456"
        val expectedLoginResult = flow<Resource<String>> {
            emit(Resource.Loading())
            emit(Resource.Error(message))
        }
        Mockito.`when`(appRepository.login(email, password)).thenReturn(expectedLoginResult)

        loginUseCase.invoke(email, password).test {
            Mockito.verify(appRepository).login(email, password)

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