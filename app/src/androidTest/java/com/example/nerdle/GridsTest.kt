package com.example.nerdle

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.button.MaterialButton
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GridsTest {
    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.nerdle", appContext.packageName)
    }

    @Test
    fun isGridsDisplayed() {
        onView(withId(R.id.blocks_recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun strokeWidthChange() {
        // test if stroke width changes when clicked
        onView(withId(R.id.blocks_recycler_view))
            .perform(actionOnItemAtPosition<GameAdapter.GameViewHolder>(2, click()))
            .check(
                matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.number_material_button), isDisplayed(), hasStrokeWidth(5)
                        )
                    )
                )
            )

        // test if stroke width changes back when clicked again
        onView(withId(R.id.blocks_recycler_view))
            .perform(actionOnItemAtPosition<GameAdapter.GameViewHolder>(2, click()))
            .check(
                matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.number_material_button), isDisplayed(), hasStrokeWidth(0)
                        )
                    )
                )
            )

        // test is stroke width changes when clicked on another button
        onView(withId(R.id.blocks_recycler_view))
            .perform(actionOnItemAtPosition<GameAdapter.GameViewHolder>(2, click()))
            .perform(actionOnItemAtPosition<GameAdapter.GameViewHolder>(3, click()))
            .perform(actionOnItemAtPosition<GameAdapter.GameViewHolder>(2, scrollTo()))
            .check(
                matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.number_material_button), isDisplayed(), hasStrokeWidth(0)
                        )
                    )
                )
            )
    }

    @Test
    fun clickableChange() {
        // generate 5*6 grids
        while (true) {
            var itemCount = 0
            onView(withId(R.id.blocks_recycler_view)).check { view, _ ->
                val recyclerView = view as RecyclerView
                itemCount = recyclerView.adapter?.itemCount ?: 0
            }

            if (itemCount == 30) {
                break // Exit the loop if item count is 30 or more
            }

            onView(withText("Reset")).perform(click())
            Thread.sleep(500)
        }

        // test if only first row is clickable
        // TODO: test if only first row is clickable

        // test if first row is not clickable after "Enter" is clicked
        // 1. enter valid calculation
        onView(withId(R.id.blocks_recycler_view)).perform(
            actionOnItemAtPosition<GameAdapter.GameViewHolder>(0, click())
        )
        onView(withTagValue(`is`("material-button_1"))).perform(click())

        onView(withId(R.id.blocks_recycler_view)).perform(
            actionOnItemAtPosition<GameAdapter.GameViewHolder>(1, click())
        )
        onView(withTagValue(`is`("material-button_+"))).perform(click())

        onView(withId(R.id.blocks_recycler_view)).perform(
            actionOnItemAtPosition<GameAdapter.GameViewHolder>(2, click())
        )
        onView(withTagValue(`is`("material-button_2"))).perform(click())

        onView(withId(R.id.blocks_recycler_view)).perform(
            actionOnItemAtPosition<GameAdapter.GameViewHolder>(3, click())
        )
        onView(withTagValue(`is`("material-button_="))).perform(click())

        onView(withId(R.id.blocks_recycler_view)).perform(
            actionOnItemAtPosition<GameAdapter.GameViewHolder>(4, click())
        )
        onView(withTagValue(`is`("material-button_3"))).perform(click())

        // 2. click on "Enter"
        onView(withText("Enter")).perform(click())

        // 3. check if first row is disabled and second row is enabled
        // TODO: check if first row is disabled

    }

    private fun hasStrokeWidth(expectedWidth: Int): Matcher<Any> {
        return object : TypeSafeMatcher<Any>() {
            override fun matchesSafely(item: Any?): Boolean {
                if (item is MaterialButton) {
                    Log.d("StrokeWidthTest", "stroke width: ${item.strokeWidth}")
                    return item.strokeWidth == expectedWidth
                }
                Log.w("StrokeWidthTest", "item is not MaterialButton")
                return false
            }

            override fun describeTo(description: Description?) {
                description?.appendText("has stroke width: $expectedWidth")
            }
        }
    }
}


