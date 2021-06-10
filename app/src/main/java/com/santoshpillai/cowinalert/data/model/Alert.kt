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
)

