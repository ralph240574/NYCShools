package com.ralphmueller.nycschools.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.data.SchoolRepo
import com.ralphmueller.nycschools.utils.MainCoroutineRule
import com.ralphmueller.nycschools.utils.createSchoolRepo
import com.ralphmueller.nycschools.utils.localSAT
import com.ralphmueller.nycschools.utils.localSchool
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DetailViewModelTest {

    private lateinit var detailViewModel: DetailViewModel

    private lateinit var schoolRepo: SchoolRepo

    private val mockWebServer = MockWebServer()


    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        schoolRepo = createSchoolRepo(mockWebServer)
        runBlocking {
            schoolRepo.localDataSource.insertLocalSchool(localSchool)
            schoolRepo.localDataSource.insertLocalSAT(localSAT)
        }
        detailViewModel = DetailViewModel(schoolRepo)
    }

    @After
    fun closeDb() {
        schoolRepo.closeDb()
        mockWebServer.shutdown()
    }

    @Test
    fun getSchool() {
        runTest(UnconfinedTestDispatcher()) {

            detailViewModel.uiState.test {
                detailViewModel.getSchool(dbn = localSchool.dbn)
                val result = awaitItem()
                assertThat(result.loading).isTrue()

                val result2 = awaitItem()
                assertThat(result2.loading).isFalse()
                assertThat(result2.school).isEqualTo(localSchool.toSchool(localSAT))
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}
