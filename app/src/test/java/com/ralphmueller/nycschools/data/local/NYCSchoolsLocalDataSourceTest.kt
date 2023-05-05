package com.ralphmueller.nycschools.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.data.Result
import com.ralphmueller.nycschools.utils.localSAT
import com.ralphmueller.nycschools.utils.localSchool
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class NYCSchoolsLocalDataSourceTest {

    private lateinit var localDataSource: NYCSchoolsLocalDataSource
    private lateinit var database: LocalDataBase

    @Before
    fun createRepository() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDataBase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = NYCSchoolsLocalDataSource(database = database)

//        runBlocking {
//            batchItems.forEachIndexed { index, it ->
//                localBatchCountDataSource.insertBatchItem(it, index)
//            }
//        }
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun insertLocalSchool() = runTest(UnconfinedTestDispatcher()) {
        localDataSource.insertLocalSchool(localSchool = localSchool)
        val result = localDataSource.getLocalSchools()
        val data = (result as Result.Success).data
        assertThat(data.size).isEqualTo(1)
        assertThat(data).contains(localSchool)
    }

    @Test
    fun insertLocalSchools() = runTest(UnconfinedTestDispatcher()) {
        localDataSource.insertLocalSchool(localSchool = localSchool)
        localDataSource.insertLocalSchool(localSchool = localSchool.copy(dbn = "2"))
        localDataSource.insertLocalSchool(localSchool = localSchool.copy(dbn = "3"))

        val result = localDataSource.getLocalSchools()
        val data = (result as Result.Success).data
        assertThat(data.size).isEqualTo(3)
        assertThat(data).contains(localSchool)
        assertThat(data).contains(localSchool.copy(dbn = "2"))
        assertThat(data).contains(localSchool.copy(dbn = "3"))
    }

    @Test
    fun insertLocalSAT() = runTest(UnconfinedTestDispatcher()) {
        localDataSource.insertLocalSAT(localSAT = localSAT)
        val result = localDataSource.getLocalSATs()
        val data = (result as Result.Success).data
        assertThat(data.size).isEqualTo(1)
        assertThat(data).contains(localSAT)
    }

    @Test
    fun insertLocalSATs() = runTest(UnconfinedTestDispatcher()) {
        localDataSource.insertLocalSAT(localSAT = localSAT)
        localDataSource.insertLocalSAT(localSAT = localSAT.copy(dbn = "2"))
        localDataSource.insertLocalSAT(localSAT = localSAT.copy(dbn = "3"))

        val result = localDataSource.getLocalSATs()
        val data = (result as Result.Success).data
        assertThat(data.size).isEqualTo(3)
        assertThat(data).contains(localSAT)
        assertThat(data).contains(localSAT.copy(dbn = "2"))
        assertThat(data).contains(localSAT.copy(dbn = "3"))
    }

    @Test
    fun getSchools() = runTest(UnconfinedTestDispatcher()) {
        localDataSource.insertLocalSchool(localSchool = localSchool)
        localDataSource.insertLocalSchool(localSchool = localSchool.copy(dbn = "2"))
        localDataSource.insertLocalSchool(localSchool = localSchool.copy(dbn = "3"))

        localDataSource.insertLocalSAT(localSAT = localSAT)
        localDataSource.insertLocalSAT(localSAT = localSAT.copy(dbn = "2"))
        localDataSource.insertLocalSAT(localSAT = localSAT.copy(dbn = "3"))

        val result = localDataSource.getSchools()
        val data = (result as Result.Success).data
        assertThat(data.size).isEqualTo(3)

        assertThat(data).contains(localSchool.toSchool(localSAT = localSAT))
        assertThat(data).contains(localSchool.copy(dbn = "2").toSchool(localSAT = localSAT))
        assertThat(data).contains(localSchool.copy(dbn = "3").toSchool(localSAT = localSAT))

    }

}