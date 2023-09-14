package com.mongodb.mongoize.android.screens.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongodb.mongoize.RealmRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class RegistrationViewModel : ViewModel() {

    private val repo = RealmRepo()
    val registrationSuccess = MutableLiveData<Boolean>()

    fun register(surname: String, firstName: String, dateOfBirth: LocalDate, email: String, phoneNumber: Long, gender: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.registration(surname = surname,
                    firstName = firstName,
                    dateOfBirth = dateOfBirth,
                    email = email,
                    phoneNumber = phoneNumber,
                    gender = gender,
                    password = password
                ).run {
                    withContext(Dispatchers.Main) {
                        registrationSuccess.value = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    registrationSuccess.value = false
                }
            }
        }
    }

}