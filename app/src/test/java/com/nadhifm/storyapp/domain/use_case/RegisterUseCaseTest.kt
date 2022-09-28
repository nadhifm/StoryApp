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
class RegisterUseCaseTest {

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setUp() {
        registerUseCase = RegisterUseCase(appRepository)
    }

    @Test
    fun `when Register Success Should Return Success and Data Not Null`() = runTest {
        val message = "Email is already taken"
        val name = "user"
        val email = "user@example.com"
        val password = "123456"
        val expectedRegisterResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(message))
        }
        Mockito.`when`(appRepository.register(name, email, password)).thenReturn(expectedRegisterResult)

        registerUseCase(name, email, password).test {
            Mockito.verify(appRepository).register(name, email, password)

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
    fun `when Register Fail Should Return Error and Message Not Null`() = runTest {
        val message = "User created"
        val name = "user"
        val email = "user@example.com"
        val password = "123456"
        val expectedRegisterResult = flow<Resource<String>> {
            emit(Resource.Loading())
            emit(Resource.Error(message))
        }
        Mockito.`when`(appRepository.register(name, email, password)).thenReturn(expectedRegisterResult)

        registerUseCase(name, email, password).test {
            Mockito.verify(appRepository).register(name, email, password)

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