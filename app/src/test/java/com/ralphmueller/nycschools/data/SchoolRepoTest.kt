package com.ralphmueller.nycschools.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.data.local.LocalDataBase
import com.ralphmueller.nycschools.data.local.NYCSchoolsLocalDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteService
import com.ralphmueller.nycschools.utils.MainCoroutineRule
import com.ralphmueller.nycschools.utils.enqueueResponse
import com.ralphmueller.nycschools.utils.localSAT
import com.ralphmueller.nycschools.utils.localSchool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SchoolRepoTest {

    private val mockWebServer = MockWebServer()

    private lateinit var repo: SchoolRepo

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NYCSchoolsRemoteService::class.java)


    private val localSchool1 = localSchool.copy(dbn = "02M260")
    private val localSchool2 = localSchool.copy(dbn = "21K728")
    private val localSchool3 = localSchool.copy(dbn = "08X282")

    private val localSAT1 = localSAT.copy(dbn = "02M260")
    private val localSAT2 = localSAT.copy(dbn = "21K728")
    private val localSAT3 = localSAT.copy(dbn = "08X282")


    @Before
    fun createRepository() {
        repo = createSchoolRepo()
        runBlocking {
            repo.localDataSource.insertLocalSchool(localSchool = localSchool1)
            repo.localDataSource.insertLocalSchool(localSchool = localSchool2)
            repo.localDataSource.insertLocalSchool(localSchool = localSchool3)
            repo.localDataSource.insertLocalSAT(localSAT = localSAT1)
            repo.localDataSource.insertLocalSAT(localSAT = localSAT2)
            repo.localDataSource.insertLocalSAT(localSAT = localSAT3)
        }
    }


    @After
    fun tearDown() {
        repo.closeDb()
        mockWebServer.shutdown()
    }

    private fun createSchoolRepo(): SchoolRepo {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDataBase::class.java,
        ).allowMainThreadQueries().build()
        val remoteDataSource = NYCSchoolsRemoteDataSource(api)
        val localDataSource = NYCSchoolsLocalDataSource(database = database)

        return SchoolRepo(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            Dispatchers.Main
        )
    }


    @Test
    fun getSchoolsLocalOnly() = runTest(UnconfinedTestDispatcher()) {
        mockWebServer.enqueue(MockResponse().setResponseCode(304))
        mockWebServer.enqueue(MockResponse().setResponseCode(304))

        repo.getSchools().test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).hasSize(3)
            assertThat(result.data).contains(localSchool1.toSchool(localSAT1))
            assertThat(result.data).contains(localSchool2.toSchool(localSAT2))
            assertThat(result.data).contains(localSchool3.toSchool(localSAT3))
            awaitComplete()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getSchoolsRemote() = runTest(UnconfinedTestDispatcher()) {
        mockWebServer.enqueueResponse("nycschools.json", 200)
        mockWebServer.enqueueResponse("nycsats.json", 200)

        repo.getSchools().test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).hasSize(3)
            val result2 = awaitItem() as Result.Success
            assertThat(result2.data).hasSize(440)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getSchoolsRemoteOnly() = runTest(UnconfinedTestDispatcher()) {
        mockWebServer.enqueueResponse("nycschools.json", 200)
        mockWebServer.enqueue(MockResponse().setResponseCode(304))

        repo.getSchools().test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).hasSize(3)
            val result2 = awaitItem() as Result.Success
            assertThat(result2.data).hasSize(440)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getSATSRemoteOnly() = runTest(UnconfinedTestDispatcher()) {
        mockWebServer.enqueue(MockResponse().setResponseCode(304))
        mockWebServer.enqueueResponse("nycsats.json", 200)

        repo.getSchools().test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).hasSize(3)
            val result2 = awaitItem() as Result.Success
            assertThat(result2.data).hasSize(3)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getNoSchools() = runTest(UnconfinedTestDispatcher()) {
        repo.clearDb()
        mockWebServer.enqueue(MockResponse().setResponseCode(304))
        mockWebServer.enqueue(MockResponse().setResponseCode(304))

        repo.getSchools().test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            println(awaitItem())
            awaitComplete()
            cancelAndConsumeRemainingEvents()
        }
    }
}
