package com.ralphmueller.nycschools.data.remote


import com.ralphmueller.nycschools.data.remote.model.RDSAT
import com.ralphmueller.nycschools.data.remote.model.RDSchool
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface NYCSchoolsRemoteService {

    @Headers("Content-Type: application/json")
    @GET("resource/s3k6-pzi2.json")
    suspend fun getNYCSchools(): Response<List<RDSchool>>

    @Headers("Content-Type: application/json")
    @GET("resource/s3k6-pzi2.json")
    suspend fun getNYCSATs(): Response<List<RDSAT>>

}