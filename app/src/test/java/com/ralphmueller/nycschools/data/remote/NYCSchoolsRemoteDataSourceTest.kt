package com.ralphmueller.nycschools.data.remote


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.data.Result
import com.ralphmueller.nycschools.utils.enqueueResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment.getApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class NYCSchoolsRemoteDataSourceTest {

    private val mockWebServer = MockWebServer()

    var cacheSize = 4 * 1024 * 1024

    var cache = Cache(
        File(getApplication().getCacheDir(), "cache"),
        cacheSize.toLong()
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .cache(cache = cache)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NYCSchoolsRemoteService::class.java)

    private lateinit var nycSchoolsRemoteDataSource: NYCSchoolsRemoteDataSource

    @Before
    fun createService() {
        nycSchoolsRemoteDataSource = NYCSchoolsRemoteDataSource(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun getSchools() {
        mockWebServer.enqueueResponse("nycschools.json", 200)

        runTest(UnconfinedTestDispatcher()) {
            val result = nycSchoolsRemoteDataSource.getNycSchools()
            val schools = (result as Result.Success).data
            assertThat(schools.size).isEqualTo(440)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getSATs() {
        mockWebServer.enqueueResponse("nycsats.json", 200)

        runTest(UnconfinedTestDispatcher()) {
            val result = nycSchoolsRemoteDataSource.getNycSats()
            val schools = (result as Result.Success).data
            assertThat(schools.size).isEqualTo(478)
        }
    }
}