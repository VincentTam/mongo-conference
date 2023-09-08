package com.mongodb.mongoize

import CommonFlow
import asCommonFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class RealmRepo {

    private val schemaClass = setOf(UserInfo::class, AppointmentInfo::class)

    private val appService by lazy {
        val appConfiguration =
            AppConfiguration.Builder(appId = "application-0-yjxbg").log(LogLevel.ALL).build()
        App.create(appConfiguration)
    }

    private val realm by lazy {
        val user = appService.currentUser!!

        val config =
            SyncConfiguration.Builder(user, schemaClass).name("conferenceInfo").schemaVersion(1)
                .initialSubscriptions { realm ->
                    add(realm.query<UserInfo>(), name = "user info", updateExisting = true)
                    add(realm.query<AppointmentInfo>(), name = "appointment info", updateExisting = true)
                    add(
                        realm.query<AppointmentInfo>(),
                        name = "appointment info",
                        updateExisting = true
                    )
                }.waitForInitialRemoteData().build()
        Realm.open(config)
    }

    suspend fun login(email: String, password: String): User {
        return appService.login(Credentials.emailPassword(email, password))
    }

    suspend fun registration(password: String, email: String) {
        appService.emailPasswordAuth.registerUser(email = email, password =  password)
    }

    fun getUserProfile(): Flow<UserInfo?> {

        println("State: ${realm.subscriptions.state}")
        println("State: ${
            realm.subscriptions.forEach {
                println("State Query: ${it.name} --- ${it.queryDescription} -- ${it.objectType}")
            }
        }")


        println("getUserProfile called")

        val count = realm.query<UserInfo>().count().find()
        println("getUserProfile userCount $count")

        val userId = appService.currentUser!!.id

        println("getUserProfile userId $userId")

        val user = realm.query<UserInfo>("_id = $0", userId).asFlow().map {
            println("getUserProfile userId ${it.list.size}")
            it.list.firstOrNull()
        }

        return user
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return flowOf(appService.currentUser != null)
    }

    suspend fun saveUserInfo(
        surname: String,
        firstName: String,
        dateOfBirth: LocalDateTime,
        phoneNumber: String,
        specification: String,
        isReceptionist: Boolean,
        gender: String,
        workingHours: List<TimeSlot>,
        isActive: Boolean
    ) {
        withContext(Dispatchers.Default) {
            if (appService.currentUser != null) {
                val userId = appService.currentUser!!.id
                realm.write {
                    var user = query<UserInfo>("_id = $0", userId).first().find()
                    if (user != null) {
                        user = findLatest(user)!!.also {
                            it.surname = surname
                            it.firstName = firstName
                            it.dateOfBirth = dateOfBirth
                            it.phoneNumber = phoneNumber.toLongOrNull()
                            it.specification = specification
                            it.isReceptionist = isReceptionist
                            it.gender = gender
                            it.workingHours = workingHours
                            it.isActive = isActive
                        }
                        copyToRealm(user)
                    }
                }
            }
        }
    }

    suspend fun doLogout() {
        appService.currentUser?.logOut()
    }

    suspend fun addAppointment(doctor: ObjectId, patient: ObjectId, time: LocalDateTime) {
        withContext(Dispatchers.Default) {
            realm.write {
                val appointmentInfo = AppointmentInfo().apply {
                    this.doctor = doctor
                    this.patient = patient
                    this.time = time
                }
                copyToRealm(appointmentInfo)
            }
        }
    }

    suspend fun getAppointmentLists(): CommonFlow<List<AppointmentInfo>> {
        return withContext(Dispatchers.Default) {
            realm.query<AppointmentInfo>().asFlow().map {
                it.list
            }
        }.asCommonFlow()
    }

    suspend fun cancelAppointment(appointmentId: ObjectId, isCancelled: Boolean) {
        return withContext(Dispatchers.Default) {
            val appointment = realm.query<AppointmentInfo>("_id = $0", appointmentId).first().find()
            if (appointment != null) {
                realm.write {
                    (findLatest(appointment) as AppointmentInfo).run {
                        this.isCancelled = true
                        copyToRealm(this)
                    }
                }
            }

        }
    }

}