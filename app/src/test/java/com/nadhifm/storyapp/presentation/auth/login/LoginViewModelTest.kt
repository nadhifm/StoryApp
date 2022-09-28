package com.nadhifm.storyapp.presentation.auth.login

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.use_case.LoginUseCase
import com.nadhifm.storyapp.utils.MainCoroutineRule
import com.nadhifm.storyapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var loginUseCase: LoginUseCase

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(loginUseCase)
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
        `when`(loginUseCase(email, password)).thenReturn(expectedLoginResult)

        loginViewModel.loginResult.test {
            loginViewModel.login(email, password)
            Mockito.verify(loginUseCase).invoke(email, password)

            var result = awaitItem()
            Assert.assertTrue(result is Resource.Loading)

            result = awaitItem()
            Assert.assertTrue(result is Resource.Success)
            Assert.assertNotNull(result.data)
            Assert.assertEquals(message, result.data)
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
        `when`(loginUseCase(email, password)).thenReturn(expectedLoginResult)

        loginViewModel.loginResult.test {
            loginViewModel.login(email, password)
            Mockito.verify(loginUseCase).invoke(email, password)

            var result = awaitItem()
            Assert.assertTrue(result is Resource.Loading)

            result = awaitItem()
            Assert.assertTrue(result is Resource.Error)
            Assert.assertNotNull(result.message)
            Assert.assertEquals(message, result.message)
        }
    }
}