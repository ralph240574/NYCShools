package com.ralphmueller.nycschools.data.remote

import com.ralphmueller.nycschools.data.Result
import com.ralphmueller.nycschools.data.remote.model.RDSAT
import com.ralphmueller.nycschools.data.remote.model.RDSchool


class NYCSchoolsRemoteDataSource(
    private val nYCSchoolsRemoteService: NYCSchoolsRemoteService,
) {

    suspend fun getNycSchools(): Result<List<RDSchool>> =
        safeApiCall(
            call = {
                val response = nYCSchoolsRemoteService.getNYCSchools()
                if (response.isSuccessful) {
                    val rdschools = response.body()
                    if (rdschools != null) {
                        return@safeApiCall Result.Success(rdschools)
                    }
                } else if (response.code() == 304) {
                    return@safeApiCall Result.Success(emptyList())
                }
                Result.Error(Exception(response.message()))
            })

    suspend fun getNycSats(): Result<List<RDSAT>> =
        safeApiCall(
            call = {
                val response = nYCSchoolsRemoteService.getNYCSATs()
                if (response.isSuccessful) {
                    val rdsats = response.body()
                    if (rdsats != null) {
                        return@safeApiCall Result.Success(rdsats)
                    }
                } else if (response.code() == 304) {
                    return@safeApiCall Result.Success(emptyList())
                }
                return@safeApiCall Result.Error(Exception("Something is wrong :${response.body()}"))
            })
}