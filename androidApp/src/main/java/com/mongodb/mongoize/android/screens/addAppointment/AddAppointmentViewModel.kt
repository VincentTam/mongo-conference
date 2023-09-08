package com.mongodb.mongoize.android.screens.addAppointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.RealmRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddAppointmentViewModel : ViewModel() {

    private val repo = RealmRepo()

    fun addConference(name: String, location: String, startDate: String, endDate: String) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.addAppointment(name, location, startDate, endDate)
        }
    }
}