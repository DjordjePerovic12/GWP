package com.global_water_polo.play_abroad.presentation.auth.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.global_water_polo.play_abroad.fakes.FakeNavigator
import com.global_water_polo.play_abroad.fakes.FakeStringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
Unit Tests for LoginViewModel, run on Android host and iOS simulator with ./gradlew :composeApp:allTests

Key test coverage:
- Email validation
- Password validation
- Login attempt states
- Error handling

Validation messages come from StringProvider, which reads Compose resources.
Tests use FakeStringProvider so every message resolves to a fixed string.

Once a real API call is added in LoginViewModel, update onLoginClickWithValidInputTriggersSuccess
(check that the token is saved in DataStore etc.)
 **/

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var navigator: FakeNavigator

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        navigator = FakeNavigator()
        viewModel = LoginViewModel(FakeStringProvider(), navigator)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onEmailChangedWithValidEmailUpdatesStateCorrectly() = runTest {
        val email = "test@example.com"
        viewModel.onEvent(LoginEvent.OnEmailChanged(email))
        val state = viewModel.viewStateFlow.first()
        assertEquals(email, state.email)
        assertNull(state.emailError)
    }

    @Test
    fun onEmailChangedWithInvalidEmailUpdatesStateWithError() = runTest {
        val email = "invalid-email"
        viewModel.onEvent(LoginEvent.OnEmailChanged(email))
        val state = viewModel.viewStateFlow.first()
        assertEquals(email, state.email)
        assertNotNull(state.emailError)
    }

    @Test
    fun emptyEmailShowsRequiredError() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged(""))
        assertNotNull(viewModel.viewStateFlow.first().emailError)
    }

    @Test
    fun emailWithSpacesShowsFormatError() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test @ example.com"))
        assertNotNull(viewModel.viewStateFlow.first().emailError)
    }

    @Test
    fun emailWithoutDomainShowsFormatError() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test@"))
        assertNotNull(viewModel.viewStateFlow.first().emailError)
    }

    @Test
    fun onPasswordChangedWithValidPasswordUpdatesStateCorrectly() = runTest {
        val password = "ValidPass123@"
        viewModel.onEvent(LoginEvent.OnPasswordChanged(password))
        val state = viewModel.viewStateFlow.first()
        assertEquals(password, state.password)
        assertNull(state.passwordError)
    }

    @Test
    fun onPasswordChangedWithShortPasswordUpdatesStateWithError() = runTest {
        val password = "short"
        viewModel.onEvent(LoginEvent.OnPasswordChanged(password))
        val state = viewModel.viewStateFlow.first()
        assertEquals(password, state.password)
        assertNotNull(state.passwordError)
    }

    @Test
    fun emptyPasswordShowsRequiredError() = runTest {
        viewModel.onEvent(LoginEvent.OnPasswordChanged(""))
        assertNotNull(viewModel.viewStateFlow.first().passwordError)
    }

    @Test
    fun onLoginClickWithValidInputTriggersSuccess() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test@example.com"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("ValidPass123@"))
        viewModel.onEvent(LoginEvent.OnLoginClick)
        assertTrue(viewModel.viewStateFlow.first().isSuccess)
    }

    @Test
    fun onLoginClickWithBothFieldsEmptySetsFailureState() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged(""))
        viewModel.onEvent(LoginEvent.OnPasswordChanged(""))
        viewModel.onEvent(LoginEvent.OnLoginClick)
        val state = viewModel.viewStateFlow.first()
        assertFalse(state.isSuccess)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
    }

    @Test
    fun onLoginClickWithInvalidInputSetsFailureState() = runTest {
        viewModel.onEvent(LoginEvent.OnEmailChanged("invalid-email"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("short"))
        viewModel.onEvent(LoginEvent.OnLoginClick)
        val state = viewModel.viewStateFlow.first()
        assertFalse(state.isSuccess)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
    }

    @Test
    fun navigateBackCallsNavigatorUp() = runTest {
        viewModel.onEvent(LoginEvent.NavigateBack)
        assertEquals(1, navigator.navigateUpCount)
    }
}
