package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt
    lateinit var database: RemindersDatabase
    lateinit var reminderData:ReminderDTO
    @Before
    fun createTestDB(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RemindersDatabase::class.java)
            .build()

        reminderData = ReminderDTO("Have a Coffee",
        "White Chocoloate Mocha",
        "Starbucks",
        232.6578,223.36728,
        "12345")
    }

    @After
    fun closeDBAfterTest(){
        database.close()
    }

    @Test
    fun testInsert_Get_Delete_Reminders_test(){
        runBlockingTest {
            database.reminderDao().saveReminder(reminderData)
            val reminderList = database.reminderDao().getReminders()
            assertEquals(1, reminderList.size)
            val reminderDTO = database.reminderDao().getReminderById("12345")
            assertEquals("Starbucks", reminderDTO?.location)
            database.reminderDao().deleteAllReminders()
            val reminderListAfterDelete = database.reminderDao().getReminders()
            assertEquals(0, reminderListAfterDelete.size)
        }

    }

}