package com.sparkingpathshala.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.sparkingpathshala.app.data.local.QuestionEntity
import com.sparkingpathshala.app.data.local.UserEntity
import com.sparkingpathshala.app.data.repository.AppRepository
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.quiz.QuizPlayScreen
import com.sparkingpathshala.app.ui.theme.SparkingPathshalaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class QuizFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeRepository : FakeAppRepository() {
        override fun getUser(): Flow<UserEntity?> = flowOf(UserEntity("1", "Sajjan", "student@sparkingpathshala.in", "123", "Class 10", "token"))
        
        override fun getQuestions(quizId: String): Flow<List<QuestionEntity>> {
            return flowOf(
                listOf(
                    QuestionEntity("q1", quizId, "Question number one?", "A1", "B1", "C1", "D1", 0, "Exp 1"),
                    QuestionEntity("q2", quizId, "Question number two?", "A2", "B2", "C2", "D2", 1, "Exp 2"),
                    QuestionEntity("q3", quizId, "Question number three?", "A3", "B3", "C3", "D3", 2, "Exp 3"),
                    QuestionEntity("q4", quizId, "Question number four?", "A4", "B4", "C4", "D4", 3, "Exp 4"),
                    QuestionEntity("q5", quizId, "Question number five?", "A5", "B5", "C5", "D5", 0, "Exp 5"),
                    QuestionEntity("q6", quizId, "Question number six?", "A6", "B6", "C6", "D6", 1, "Exp 6")
                )
            )
        }
    }

    @Test
    fun testQuizPlayScreenDisplaysQuestions() {
        val fakeRepo = FakeRepository()
        val viewModel = DashboardViewModel(fakeRepo)

        composeTestRule.setContent {
            SparkingPathshalaTheme {
                QuizPlayScreen(
                    viewModel = viewModel,
                    quizId = "practice_Class 10_Physics_5",
                    onBack = {}
                )
            }
        }

        // Verify that UI elements are correctly rendered and mock questions show up on device screen
        composeTestRule.onNodeWithText("Question number", substring = true).assertExists()
        composeTestRule.onNodeWithText("Submit Answer", substring = true).assertExists()
    }
}
