package com.udacity.project4.locationreminders.data

import android.util.Log
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
    companion object{
        const val TAG = "FakeDataSource"
    }
    private var shouldReturnError = false

    private val remindersList = mutableListOf<ReminderDTO>()
//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if(shouldReturnError){
            Result.Error("Reminders not found",404)
        }else{
            Result.Success(remindersList)
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        Log.i(TAG,"Reminder Data Saved")
        remindersList.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if(shouldReturnError){
            Result.Error("No Reminder found",404)
        }else{
            val reminder = ReminderDTO("Have a coffee","White CHocolate Mocha","StarBucks",23.432,434.2323,"1234567")
            Result.Success(reminder)
        }
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
    }



    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }


}