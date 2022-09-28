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
class GetNameUseCaseTest {

    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var getNameUseCase: GetNameUseCase

    @Before
    fun setUp() {
        getNameUseCase = GetNameUseCase(appRepository)
    }

    @Test
    fun `when User Already Login Should Return Success and Name`() = runTest {
        val name = "user"
        val expectedName = flow {
            emit(Resource.Loading())
            emit(Resource.Success("user"))
        }
        Mockito.`when`(appRepository.getName()).thenReturn(expectedName)

        getNameUseCase().test {
            Mockito.verify(appRepository).getName()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(name, result.data)

            awaitComplete()
        }
    }
}