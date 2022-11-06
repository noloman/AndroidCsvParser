package com.nulltwenty.rabobankcsvparser.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.nulltwenty.rabobankcsvparser.data.di.DataModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(DataModule::class)
class MainActivityTest {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Test
    fun givenAFakeRepositoryWithSameData_whenTheRecyclerViewIsShown_itShouldShowTheFullNames() {
        checkPresenceOf("Name: Theo Jansen")
        checkPresenceOf("Name: Fiona de Vries")
        checkPresenceOf("Name: Petra Boersma")
    }

    private fun checkPresenceOf(text: String) {
        onView(withId(com.nulltwenty.rabobankcsvparser.R.id.recyclerView)).check(
            matches(
                hasDescendant(withText(text))
            )
        )
    }
}