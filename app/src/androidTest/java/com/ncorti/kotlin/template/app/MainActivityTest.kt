package com.ncorti.kotlin.template.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = activityScenarioRule<MainActivity>()

    @Test
    fun mainActivity_dashboardIsDisplayed() {
        onView(withId(R.id.text_present_count)).check(matches(isDisplayed()))
        onView(withId(R.id.text_total_count)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))
        onView(withId(R.id.button_kiosk)).check(matches(isDisplayed()))
    }
}

