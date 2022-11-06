package com.nulltwenty.rabobankcsvparser.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
    fun givenAFakeRepositoryWithSameData_whenTheRecyclerViewIsShown_itShouldShowCompleteInformation() {
        checkPresenceOf(0, com.nulltwenty.rabobankcsvparser.R.id.name, "Name: Theo Jansen")
        checkPresenceOf(1, com.nulltwenty.rabobankcsvparser.R.id.name, "Name: Fiona de Vries")
        checkPresenceOf(2, com.nulltwenty.rabobankcsvparser.R.id.name, "Name: Petra Boersma")
        checkPresenceOf(
            0,
            com.nulltwenty.rabobankcsvparser.R.id.issueCount,
            ApplicationProvider.getApplicationContext<Context>()
                .getString(com.nulltwenty.rabobankcsvparser.R.string.issue_count, 5)
        )
        checkPresenceOf(
            1,
            com.nulltwenty.rabobankcsvparser.R.id.issueCount,
            ApplicationProvider.getApplicationContext<Context>()
                .getString(com.nulltwenty.rabobankcsvparser.R.string.issue_count, 7)
        )
        checkPresenceOf(
            2,
            com.nulltwenty.rabobankcsvparser.R.id.issueCount,
            ApplicationProvider.getApplicationContext<Context>()
                .getString(com.nulltwenty.rabobankcsvparser.R.string.issue_count, 1)
        )
    }

    private fun checkPresenceOf(position: Int, viewId: Int, text: String) {
        onView(
            withRecyclerView(com.nulltwenty.rabobankcsvparser.R.id.recyclerView).atPositionOnView(
                position, viewId
            )
        ).check(matches(withText(text)))
    }
}