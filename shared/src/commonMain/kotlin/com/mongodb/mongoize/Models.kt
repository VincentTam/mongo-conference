package com.mongodb.mongoize

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime


class UserInfo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var surname: String = ""
    var firstName: String = ""
    var dateOfBirth: LocalDateTime = LocalDateTime(1970,1,1,0,0,0,0)
    var email: String = ""
    var phoneNumber: Long? = null
    var specification: String? = null
    var isAdmin: Boolean = false
    var isReceptionist: Boolean = false
    var gender: String = "H"
    var workingHours: List<TimeSlot> = listOf()
    var isActive: Boolean = true
}

class TimeSlot {
    var start: LocalTime = LocalTime(9,0,0,0)
    var end: LocalTime = LocalTime(12,0,0,0)
}

class AppointmentInfo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var doctor: ObjectId? = null
    var patient: ObjectId? = null
    var time: LocalDateTime? = null
    var notes: String = ""
    var isCancelled: Boolean = false
    var arrivalTime: LocalDateTime? = null
    var isAccepted: Boolean = false
    var prescription: List<PrescriptionLine> = listOf()
}

class PrescriptionLine {
    var medicine: String = ""
    var qty: Int = 0
    var mode: String = ""
}
