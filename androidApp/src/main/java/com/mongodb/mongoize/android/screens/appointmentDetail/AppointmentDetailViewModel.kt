package com.mongodb.mongoize.android.screens.appointmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.AppointmentInfo
import com.mongodb.mongoize.RealmRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class AppointmentDetailViewModel() : ViewModel() {

    val repo = RealmRepo()
    lateinit var appointmentId: ObjectId

    fun updateAppointmentId(id: String) {
        appointmentId = ObjectId(id)
    }

    val appointments: LiveData<List<AppointmentInfo>> = liveData {
        emitSource(repo.getAppointmentListAsPatient().asLiveData(Dispatchers.IO))
    }

    val activeAppointments: LiveData<List<AppointmentInfo>> = liveData {
        emitSource(repo.getActiveAppointmentListAsPatient().asLiveData(Dispatchers.IO))
    }

    fun updateAppointmentStatus(appointmentId: ObjectId, state: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.cancelAppointment(appointmentId, state)
        }
    }
}