package com.mongodb.mongoize.android.screens.addAppointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.RealmRepo
import com.mongodb.mongoize.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class AddAppointmentViewModel : ViewModel() {
    private val repo = RealmRepo()
    fun addAppointment(doctor: String, time: LocalDateTime) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addAppointment(doctor = doctor, patient = repo.getUserId(), time = time)
        }
    }

    val doctors: LiveData<List<UserInfo>> = liveData {
        emitSource(repo.getDoctorList().asLiveData(Dispatchers.IO))
    }
}