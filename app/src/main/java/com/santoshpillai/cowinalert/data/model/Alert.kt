package com.santoshpillai.cowinalert.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cowin_alert_table")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    val alertID: Long = 0L,

    @ColumnInfo(name = "alert_name")
    val name: String,

    @ColumnInfo(name = "pincode")
    val pinCode: Long,

    @ColumnInfo(name = "isCovishield")
    val isCovishield: Boolean = false,

    @ColumnInfo(name = "isCovaxin")
    val isCovaxin: Boolean = false,

    @ColumnInfo(name = "above45")
    val above45: Boolean = false,

    @ColumnInfo(name = "below45")
    val below45: Boolean = false,

    @ColumnInfo(name = "isDose1")
    val dose1: Boolean = false,

    @ColumnInfo(name = "isDose2")
    val dose2: Boolean = false,

    @ColumnInfo(name = "isFree")
    val free: Boolean = false,

    @ColumnInfo(name = "isPaid")
    val paid: Boolean = false,

    @ColumnInfo(name = "status")
    val status: String = "enabled",
) {

    fun getVaccines(): String {
        var vaccines: String = ""
        if (isCovishield && isCovaxin) {
            vaccines = "any"
        } else {
            if (isCovaxin) {
                vaccines += "covaxin"
            }
            if (isCovishield) {
                vaccines = if (vaccines == "") vaccines + "covishield" else "$vaccines,covishield"
            }
        }
        return vaccines

    }

    fun getFeeType(): String {
        var feeType: String = ""
        if (paid && free) {
            feeType = "any"
        } else {
            if (free) {
                feeType += "free"
            }
            if (paid) {
                feeType = if (feeType == "") feeType + "paid" else "$feeType,paid"
            }
        }
        return feeType
    }

    fun getDoseType(): String {
        var doseType: String = ""
        if (dose1 && dose2) {
            doseType = "any"
        } else {
            if (dose1) {
                doseType += "dose1"
            }
            if (dose2) {
                doseType = if (doseType == "") doseType + "dose2" else "$doseType,dose2"
            }
        }
        return doseType
    }

    fun getAgeGroups(): String {
        var ageGroup: String = ""
        if (above45 && below45) {
            ageGroup = "any"
        } else {
            if (above45) {
                ageGroup += ">45"
            }
            if (below45) {
                ageGroup = if (ageGroup == "") "$ageGroup<45" else "$ageGroup,<45"
            }
        }
        return ageGroup
    }

}

