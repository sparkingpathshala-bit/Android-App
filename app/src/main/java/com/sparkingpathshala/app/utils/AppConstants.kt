package com.sparkingpathshala.app.utils

/**
 * Central academic and application constants.
 */
object AppConstants {

    val ALL_CLASSES: List<String> = listOf(
        "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6",
        "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12"
    )

    val CLASS_OPTIONS_WITH_ALL: List<String> = listOf("All") + ALL_CLASSES

    val ALL_SUBJECTS: List<String> = listOf(
        "Maths", "Science", "Physics", "Chemistry", "Biology",
        "English", "Hindi", "Sanskrit", "Social Science", "Computer Science",
        "Economics", "Accountancy", "Business Studies", "General Knowledge"
    )

    val MATERIAL_CATEGORIES: List<Pair<String, String>> = listOf(
        "notes" to "Concept Notes",
        "guides" to "Reference Guides",
        "question-bank" to "Question Bank",
        "test-series" to "Test Series",
        "premium-material" to "Premium Material"
    )

    const val DEFAULT_CLASS: String = "Class 10"
    const val MAX_FILE_SIZE_MB: Float = 20.0f
}
