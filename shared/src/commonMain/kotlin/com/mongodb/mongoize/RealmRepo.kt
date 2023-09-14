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
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.mongodb.kbson.ObjectId

class RealmRepo {

    private val schemaClass = setOf(UserInfo::class, AppointmentInfo::class, PrescriptionLine::class)

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
                }.waitForInitialRemoteData().build()
        Realm.open(config)
    }

    fun getUserId() : String {
        return appService.currentUser!!.id
    }

    suspend fun login(email: String, password: String): User {
        return appService.login(Credentials.emailPassword(email, password))
    }

    suspend fun registration(
        surname: String,
        firstName: String,
        dateOfBirth: LocalDate,
        email: String,
        phoneNumber: Long,
        gender: String,
        password: String
    ) {
        appService.emailPasswordAuth.registerUser(email = email, password = password)
        addUserProfile(
            surname = surname,
            firstName = firstName,
            dateOfBirth = dateOfBirth,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender
        )
    }

    suspend fun addUserProfile(
        surname: String,
        firstName: String,
        dateOfBirth: LocalDate,
        email: String,
        phoneNumber: Long,
        gender: String
    ) {
        withContext(Dispatchers.Default) {
            if (appService.currentUser != null) {
                realm.write {
                    val userInfo = UserInfo().apply {
                        this._id = getUserId()
                        this.surname = surname
                        this.firstName = firstName
                        this.dateOfBirth = RealmInstant.from( 86400L * dateOfBirth.toEpochDays(), 0)
                        this.email = email
                        this.phoneNumber = phoneNumber
                        this.gender = gender
                    }
                    copyToRealm(userInfo)
                }
            }
        }
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

        val userId = getUserId()

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
        dateOfBirth: LocalDate,
        phoneNumber: String,
        specification: String,
        isReceptionist: Boolean,
        gender: String,
        hour1: Int,
        hour2: Int,
        hour3: Int,
        hour4: Int,
        isActive: Boolean
    ) {
        withContext(Dispatchers.Default) {
            if (appService.currentUser != null) {
                val userId = getUserId()
                realm.write {
                    var user = query<UserInfo>("_id = $0", userId).first().find()
                    if (user != null) {
                        user = findLatest(user)!!.also {
                            it.surname = surname
                            it.firstName = firstName
                            it.dateOfBirth = RealmInstant.from( 86400L * dateOfBirth.toEpochDays(), 0)
                            it.phoneNumber = phoneNumber.toLong()
                            it.specification = specification
                            it.isReceptionist = isReceptionist
                            it.gender = gender
                            it.hour1 = hour1
                            it.hour2 = hour2
                            it.hour3 = hour3
                            it.hour4 = hour4
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

    suspend fun addAppointment(doctor: String, patient: String, time: LocalDateTime) {
        withContext(Dispatchers.Default) {
            realm.write {
                val appointmentInfo = AppointmentInfo().apply {
                    this.doctor = doctor
                    this.patient = patient
                    this.time = RealmInstant.from(time.toInstant(TimeZone.currentSystemDefault()).epochSeconds, 0)
                }
                copyToRealm(appointmentInfo)
            }
        }
    }

    suspend fun getDoctorList(): CommonFlow<List<UserInfo>> {
        return withContext(Dispatchers.Default) {
            realm.query<UserInfo>("specification != $0", "").asFlow().map {
                it.list
            }
        }.asCommonFlow()
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

    suspend fun confirmAppointment(appointmentId: ObjectId) {
        return withContext(Dispatchers.Default) {
            val appointment = realm.query<AppointmentInfo>("_id = $0", appointmentId).first().find()
            if (appointment != null) {
                realm.write {
                    (findLatest(appointment) as AppointmentInfo).run {
                        this.arrivalTime = RealmInstant.from(Clock.System.now().epochSeconds, 0)
                        copyToRealm(this)
                    }
                }
            }
        }
    }
}