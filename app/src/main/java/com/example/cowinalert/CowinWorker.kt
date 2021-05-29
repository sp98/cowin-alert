package com.example.cowinalert

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QueryWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams){

    companion object{
        const val WORK_NAME = "QueryCowinAPI"
    }
    override suspend fun doWork(): Result {

        // get filters set by user
        val database = AlertDatabase.getInstance(applicationContext)
        val filters = database.alertDatabaseDao.getAllAlerts()

        // call cowin API to get all the results if user has set filters

        return try {
            if (filters.value!!.isNotEmpty()) {
                CowinAPI.retrofitService.getCowinData().enqueue( object: Callback<Centers> {
                    override fun onResponse(call: Call<Centers>, response: Response<Centers>) {
                        val centers = response.body()?.centers!!
                        val filters = filters.value!!
                        val results = matchFilter(filters, centers)
                        if (results.isNotEmpty()){
                            for (result in results){
                                database.alertDatabaseDao.insertResult(result)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Centers>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            }
            Result.success()
        }catch (e: Exception){
            println("failed with exception ${e.message}")
            return Result.retry()
        }

    }


    private fun matchFilter(filters: List<Alert>, centers: List<Center>): List<com.example.cowinalert.Result>{
        var results: List<com.example.cowinalert.Result> = ArrayList()
        for (center in centers){
            val sessions = center.sessions
            for (session in sessions){
                if (session.availableCapacity > 0){
                    for (filter in filters){
                        if (filter.pinCode == center.pincode){
                            // TODO add more matchers.
                                val result = com.example.cowinalert.Result(
                                    alertID = filter.alertID,
                                    hospitalName = center.name,
                                    address = center.address,
                                    stateName = center.stateName,
                                    districtName = center.districtName,
                                    blockName = center.blockName,
                                    feeType = center.feeType,
                                    availableCapacity = session.availableCapacity,
                                    dose1Capacity = session.availableCapacityDose1,
                                    dose2Capacity = session.availableCapacityDose2
                                )
                            results = results + listOf<com.example.cowinalert.Result>(result)
                        }
                    }
                }
            }
        }
        return results
    }

}



