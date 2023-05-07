package com.ralphmueller.nycschools.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ralphmueller.nycschools.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeSort() {
        composeTestRule.onNodeWithText("NYC School Data").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Sort").performClick()
        composeTestRule.onNodeWithText("Math Score").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reading Score").performClick()
    }

    @Test
    fun schoolListClick() {
        composeTestRule.onNodeWithTag(SCHOOL_LIST).onChildren().onFirst().performClick()
        composeTestRule.onNodeWithText("School Details").assertIsDisplayed()
    }
}
