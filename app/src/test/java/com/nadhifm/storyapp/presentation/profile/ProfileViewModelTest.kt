package com.nadhifm.storyapp.presentation.profile

import app.cash.turbine.test
import com.nadhifm.storyapp.domain.use_case.GetNameUseCase
import com.nadhifm.storyapp.domain.use_case.LogoutUseCase
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
class ProfileViewModelTest {

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineRule()

    @Mock
    private lateinit var getNameUseCase: GetNameUseCase

    @Mock
    private lateinit var logoutUseCase: LogoutUseCase

    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setUp() {
        profileViewModel = ProfileViewModel(logoutUseCase, getNameUseCase)
    }

    @Test
    fun `when User Already Login Should Return Name`() = runTest {
        val name = "user"
        val expectedName = flow {
            emit(Resource.Loading())
            emit(Resource.Success(name))
        }
        Mockito.`when`(getNameUseCase()).thenReturn(expectedName)

        profileViewModel.name.test {
            profileViewModel.getName()
            Mockito.verify(getNameUseCase).invoke()

            awaitItem()
            val result = awaitItem()
            assertEquals(name, result)
        }
    }

    @Test
    fun `when Logout Success Should Return Success and True`() = runTest {
        val expectedLogoutResult = flow {
            emit(Resource.Loading())
            emit(Resource.Success(true))
        }
        Mockito.`when`(logoutUseCase()).thenReturn(expectedLogoutResult)

        profileViewModel.logoutResult.test {
            profileViewModel.logout()
            Mockito.verify(logoutUseCase).invoke()

            var result = awaitItem()
            assertTrue(result is Resource.Loading)

            result = awaitItem()
            assertTrue(result is Resource.Success)
            assertNotNull(result.data)
            assertEquals(true, result.data)
        }
    }
}