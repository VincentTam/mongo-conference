package com.mongodb.mongoize.android.screens.addAppointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.RealmRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.mongodb.kbson.ObjectId

class AddAppointmentViewModel : ViewModel() {
    private val repo = RealmRepo()
    fun addAppointment(doctor: ObjectId, patient: ObjectId, time: LocalDateTime) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addAppointment(doctor, patient, time)
        }
    }
}