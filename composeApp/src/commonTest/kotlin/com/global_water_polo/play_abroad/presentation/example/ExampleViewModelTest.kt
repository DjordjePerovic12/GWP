package com.global_water_polo.play_abroad.presentation.example

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.utils.networking.NetworkError
import com.global_water_polo.play_abroad.core.utils.networking.Resource
import com.global_water_polo.play_abroad.domain.model.TestModel
import com.global_water_polo.play_abroad.fakes.FakeBaseRepository
import com.global_water_polo.play_abroad.fakes.FakeNavigator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleViewModelTest {

    private lateinit var repository: FakeBaseRepository
    private lateinit var navigator: FakeNavigator
    private lateinit var viewModel: ExampleViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeBaseRepository()
        navigator = FakeNavigator()
        viewModel = ExampleViewModel(repository, navigator)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun firstCollectionLoadsData() = runTest {
        viewModel.exampleState.test {
            val state = expectMostRecentItem()
            assertEquals(TestModel(id = 1, name = "Test"), state.testData)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun errorResponseShowsBackendMessageInSnackBar() = runTest {
        viewModel.snackBarChannel.test {
            viewModel.onEvent(ExampleEvent.OnEmptyResponseClick)
            assertEquals("Bad request", awaitItem())
        }
    }

    @Test
    fun errorWithoutMessageFallsBackToErrorName() = runTest {
        repository.dataResponse = Resource.Error(NetworkError.NO_INTERNET, null)
        viewModel.snackBarChannel.test {
            viewModel.onEvent(ExampleEvent.OnDataResponseClick)
            assertEquals("NO_INTERNET", awaitItem())
        }
    }

    @Test
    fun loginDemoClickNavigatesToLogin() = runTest {
        viewModel.onEvent(ExampleEvent.OnLoginDemoClick)
        assertEquals(listOf<Screen>(Screen.LoginScreen), navigator.destinations)
    }
}
