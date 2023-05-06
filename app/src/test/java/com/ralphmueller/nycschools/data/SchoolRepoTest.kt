package com.ralphmueller.nycschools.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.utils.MainCoroutineRule
import com.ralphmueller.nycschools.utils.createSchoolRepo
import com.ralphmueller.nycschools.utils.enqueueResponse
import com.ralphmueller.nycschools.utils.localSAT
import com.ralphmueller.nycschools.utils.localSchool
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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


    val localSchool1 = localSchool.copy(dbn = "02M260")
    val localSchool2 = localSchool.copy(dbn = "21K728")
    val localSchool3 = localSchool.copy(dbn = "08X282")

    val localSAT1 = localSAT.copy(dbn = "02M260")
    val localSAT2 = localSAT.copy(dbn = "21K728")
    val localSAT3 = localSAT.copy(dbn = "08X282")

    @Before
    fun createRepository() {
        repo = createSchoolRepo(mockWebServer)
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
            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun getSchoolLocalOnly() = runTest(UnconfinedTestDispatcher()) {

        repo.getSchool(dbn = localSchool1.dbn).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).isEqualTo(localSchool1.toSchool(localSAT1))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getSchoolsRemote() = runTest(UnconfinedTestDispatcher()) {
        mockWebServer.enqueueResponse("nycschools.json", 200)
        mockWebServer.enqueueResponse("nycsats.json", 200)

        repo.getSchools(forceRefresh = true).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem() as Result.Success
            assertThat(result.data).hasSize(3)
            val result2 = awaitItem() as Result.Success
            assertThat(result2.data).hasSize(440)
            cancelAndConsumeRemainingEvents()
        }
    }

}
