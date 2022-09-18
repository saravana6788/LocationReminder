package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    private val fakeDataSource: FakeDataSource = FakeDataSource()
    private val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)

    @get:Rule()
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule()
    var mainCoroutineRule = MainCoroutineRule()


    @After
    fun tearDown() {
        stopKoin()
    }


    //TODO: provide testing to the SaveReminderView and its live data objects

    @Test
    fun test_validateAndSaveReminder_happy_path() {
        var reminderTestData = ReminderDataItem("Have a Coffee",
            "White Chocolate Mocha",
            "Starbucks",
            223.54,
            454.34,
            "123"
        )
        val validationStatus = saveReminderViewModel.validateAndSaveReminder(reminderTestData)
        assertEquals(true,validationStatus)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), notNullValue())
    }

    @Test
    fun test_validateAndSaveReminder_invalid_title() {
        val reminderDataWithInvalidTitle = ReminderDataItem("","White Chocolate Mocha",
            "Starbucks",
            223.54,
            454.34,
            "1234"
        )
        val validationStatus = saveReminderViewModel.validateAndSaveReminder(reminderDataWithInvalidTitle)
        assertEquals(false,validationStatus)
        assertThat(saveReminderViewModel.showSnackBarInt.value, notNullValue())
    }

    @Test
    fun test_validateAndSaveReminder_invalid_location() {
        val reminderDataWithInvalidLocation = ReminderDataItem("Have a coffee","White Chocolate Mocha",
            "",
            223.54,
            454.34,
            "1234"
        )
        val validationStatus = saveReminderViewModel.validateAndSaveReminder(reminderDataWithInvalidLocation)
        assertEquals(false,validationStatus)
        assertThat(saveReminderViewModel.showSnackBarInt.value, notNullValue())
    }


    @Test
    fun check_loading(){
        var reminderTestData = ReminderDataItem("Have a Coffee",
            "White Chocolate Mocha",
            "Starbucks",
            223.54,
            454.34,
            "123"
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminderTestData)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }





}