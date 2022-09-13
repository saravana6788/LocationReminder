package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {



    private lateinit var repository: ReminderDataSource

    private lateinit var reminderDTO:ReminderDTO

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initRepository() {

        stopKoin()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    getApplicationContext(),
                    get() as ReminderDataSource
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }
        }

        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }

        repository = get().koin.get()


        runBlocking {
            repository.deleteAllReminders()
        }

        reminderDTO = ReminderDTO("Have a coffee","White Chocolate Mocha",
        "Starbucks", 234.645342,432.43563,"123456")

    }

   // Test the displayed data on the UI.
@Test
    fun reminders_DisplayInUI() = runBlockingTest {
        runBlocking {
            repository.saveReminder(reminderDTO)
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
            onView(withText(reminderDTO.title)).check(matches(isDisplayed()))
            onView(withText(reminderDTO.description)).check(matches(isDisplayed()))
            onView(withText(reminderDTO.location)).check(matches(isDisplayed()))

        }
    }


    //    add testing for the error messages.
    @Test
    fun reminders_showNoData_inUI() = runBlockingTest {
        runBlocking{
            repository.deleteAllReminders()
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
            onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

        }
    }


    //  test the navigation of the fragments.

    @Test
    fun navigate_to_saveReminder_test() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navigationController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navigationController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navigationController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

}