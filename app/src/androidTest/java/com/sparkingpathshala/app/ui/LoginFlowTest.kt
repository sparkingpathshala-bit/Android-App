package com.sparkingpathshala.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.sparkingpathshala.app.data.local.UserEntity
import com.sparkingpathshala.app.data.repository.AppRepository
import com.sparkingpathshala.app.ui.auth.AuthViewModel
import com.sparkingpathshala.app.ui.auth.LoginScreen
import com.sparkingpathshala.app.ui.theme.SparkingPathshalaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class LoginFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Fake repository for testing UI state binding
    class FakeRepository : FakeAppRepository() {
        override fun getUser(): Flow<UserEntity?> = flowOf(null)
        override suspend fun login(email: String, password: String): Result<UserEntity> {
            return Result.success(UserEntity("1", "Sajjan", email, "123", "Class 10", "token"))
        }
    }

    @Test
    fun testLoginFieldsAndButtonsExist() {
        val fakeRepo = FakeRepository()
        val viewModel = AuthViewModel(fakeRepo)

        composeTestRule.setContent {
            SparkingPathshalaTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToSignup = {},
                    onLoginSuccess = {}
                )
            }
        }

        // Verify logo/text and inputs are visible
        composeTestRule.onNodeWithText("Sparking Pathshala").assertExists()
        composeTestRule.onNodeWithText("Email or Mobile Number").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Log In").assertExists()
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }
}
