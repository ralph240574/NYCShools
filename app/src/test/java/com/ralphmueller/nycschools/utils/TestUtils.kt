package com.ralphmueller.nycschools.utils

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ralphmueller.nycschools.data.SchoolRepo
import com.ralphmueller.nycschools.data.local.LocalDataBase
import com.ralphmueller.nycschools.data.local.NYCSchoolsLocalDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteService
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


val client = OkHttpClient.Builder()
    .connectTimeout(2, TimeUnit.SECONDS)
    .readTimeout(2, TimeUnit.SECONDS)
    .writeTimeout(2, TimeUnit.SECONDS)
    .build()

fun createSchoolRepo(mockWebServer: MockWebServer): SchoolRepo {
    val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        LocalDataBase::class.java,
    ).allowMainThreadQueries().build()

    val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NYCSchoolsRemoteService::class.java)

    val remoteDataSource = NYCSchoolsRemoteDataSource(api)
    val localDataSource = NYCSchoolsLocalDataSource(database = database)

    return SchoolRepo(
        localDataSource = localDataSource,
        remoteDataSource = remoteDataSource,
        Dispatchers.Main
    )
}


fun MockWebServer.enqueueResponse(fileName: String, code: Int) {


    val inputStream = javaClass.classLoader!!.getResourceAsStream("api-response/$fileName")

    val source = inputStream!!.let { inputStream.source().buffer() }
    enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody(source.readString(StandardCharsets.UTF_8))
    )
}