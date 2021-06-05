package com.santoshpillai.cowinalert

import com.squareup.moshi.Json


data class Centers(
    val centers: List<Center>
)

data class Center(
    @Json(name = "center_id")
    val centerID: Double,
    val name: String,
    val address: String,
    @Json(name = "state_name")
    val stateName: String,
    @Json(name = "district_name")
    val districtName: String,
    @Json(name = "block_name")
    val blockName: String,
    val pincode: Long,
    val lat: Double,
    val long: Double,
    val from: String,
    val to: String,
    @Json(name = "fee_type")
    val feeType: String,
    val sessions: List<Session>
)

data class Session(
    @Json(name = "session_id")
    val sessionID: String,
    val date: String,
    @Json(name = "available_capacity")
    val availableCapacity: Int,
    @Json(name = "min_age_limit")
    val minAgeLimit: Int,
    val vaccine: String,
    val slots: List<String>,
    @Json(name = "available_capacity_dose1")
    val availableCapacityDose1: Int,
    @Json(name = "available_capacity_dose2")
    val availableCapacityDose2: Int,
)