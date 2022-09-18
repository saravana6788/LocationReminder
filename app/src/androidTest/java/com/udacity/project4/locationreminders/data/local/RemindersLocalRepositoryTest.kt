package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    lateinit var remindersLocalRepository: RemindersLocalRepository
    lateinit var reminderData: ReminderDTO

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initialSetUp(){
        val database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RemindersDatabase::class.java)
            .build()
        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(),Dispatchers.IO)
        reminderData = ReminderDTO("Have a Coffee",
            "White Chocoloate Mocha",
            "Starbucks",
            232.6578,223.36728,
            "12345")
    }

@Test
    fun test_getReminders_success(){
        runBlocking {
            remindersLocalRepository.saveReminder(reminderData)
            val result = remindersLocalRepository.getReminders()
            assertThat(result is Result.Success ,`is`(true))
            val resultById = remindersLocalRepository.getReminder("12345")
            assertThat(resultById is Result.Success ,`is`(true))
            remindersLocalRepository.deleteAllReminders()
            val resultAfterDelete = remindersLocalRepository.getReminder("12345")
            assertThat(resultAfterDelete is Result.Error ,`is`(true))

        }

    }

    @Test
    fun test_getReminder_error(){
        runBlocking {
            remindersLocalRepository.saveReminder(reminderData)
            val result = remindersLocalRepository.getReminders()
            assertThat(result is Result.Success ,`is`(true))
            val resultById = remindersLocalRepository.getReminder("12345fd")
            assertThat(resultById is Result.Error ,`is`(true))
            remindersLocalRepository.deleteAllReminders()
            val resultAfterDelete = remindersLocalRepository.getReminder("12345")
            assertThat(resultAfterDelete is Result.Error ,`is`(true))

        }

    }




}