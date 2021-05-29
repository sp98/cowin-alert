package com.example.cowinalert

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cowin_alert_table")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    val alertID:Long = 0L,

    @ColumnInfo(name = "alert_name")
    val name: String,

    @ColumnInfo(name= "pincode")
    val pinCode: Long,

    @ColumnInfo(name= "isCovishield")
    val isCovishield: Boolean = false,

    @ColumnInfo(name = "isCovaxin")
    val isCovaxin: Boolean = false,

    @ColumnInfo(name = "above45")
    val above45: Boolean = false,

    @ColumnInfo(name = "below45")
    val below45: Boolean = false,
)


@Entity(tableName = "cowin_result_table")
data class Result(
    @PrimaryKey(autoGenerate = true)
    val resultID:Long = 0L,

    val alertID:Long,

    @ColumnInfo(name = "hospital_name")
    val hospitalName: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "state_name")
    val stateName: String,

    @ColumnInfo(name = "district_name")
    val districtName: String,

    @ColumnInfo(name = "block_name")
    val blockName: String,

    @ColumnInfo(name = "fee_type")
    val feeType: String,

    @ColumnInfo(name = "dose_1_capacity")
    val dose1Capacity: Int,

    @ColumnInfo(name = "dose_2_capacity")
    val dose2Capacity: Int,

)