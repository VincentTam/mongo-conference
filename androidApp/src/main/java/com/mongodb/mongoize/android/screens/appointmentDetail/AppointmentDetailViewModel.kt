package com.mongodb.mongoize.android.screens.appointmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.AppointmentInfo
import com.mongodb.mongoize.RealmRepo
import org.mongodb.kbson.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppointmentDetailViewModel() : ViewModel() {

    val repo = RealmRepo()
    lateinit var appointmentId: ObjectId

    fun updateAppointmentId(id: String) {
        appointmentId = ObjectId.from(id)
    }

    val appointment: LiveData<AppointmentInfo> = liveData {
        emitSource(repo.getAppointment(appointmentId).asLiveData(Dispatchers.IO))
    }

    val selectedTalks: LiveData<List<SessionInfo>> = liveData {
        emitSource(repo.getSelectedTalks(appointmentId).asLiveData(Dispatchers.IO))
    }

    fun updateTalkStatus(talkId: ObjectId, state: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.cancelAppointment(talkId, state)
        }
    }
}