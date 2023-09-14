package com.mongodb.mongoize

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class UserInfo : RealmObject {
    @PrimaryKey
    var _id: String = ""
    var surname: String = ""
    var firstName: String = ""
    var dateOfBirth: RealmInstant = RealmInstant.from(0, 0)
    var email: String = ""
    var phoneNumber: Long = 0
    var specification: String = ""
    var isAdmin: Boolean = false
    var isReceptionist: Boolean = false
    var gender: String = "H"
    var hour1: Int = 0
    var hour2: Int = 0
    var hour3: Int = 0
    var hour4: Int = 0
    var isActive: Boolean = true
}

class AppointmentInfo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var doctor: String = ""
    var patient: String = ""
    var time: RealmInstant? = null
    var notes: String = ""
    var isCancelled: Boolean = false
    var arrivalTime: RealmInstant? = null
    var isAccepted: Boolean = false
    var prescription: RealmList<PrescriptionLine> = realmListOf<PrescriptionLine>()
    var fee: Int = 0
    var paymentMode: String = ""
}

class PrescriptionLine : RealmObject {
    var medicine: String = ""
    var qty: Int = 0
    var mode: String = ""
    val appointment: RealmResults<AppointmentInfo> by backlinks(AppointmentInfo::prescription)
}
