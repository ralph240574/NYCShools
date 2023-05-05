package com.ralphmueller.nycschools.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
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
class LocalSchoolDaoTest {

    private lateinit var database: LocalDataBase

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            LocalDataBase::class.java
        ).allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun `should insert school`() = runTest(UnconfinedTestDispatcher()) {
        database.localSchoolDao().insertLocalSchool(localSchool)
        val loaded = database.localSchoolDao().getLocalSchools()
        assertThat(loaded[0]).isEqualTo(localSchool)
    }

    @Test
    fun `should insert multiple school`() = runTest(UnconfinedTestDispatcher()) {
        database.localSchoolDao().insertLocalSchool(localSchool)
        database.localSchoolDao().insertLocalSchool(localSchool.copy(dbn = "2"))
        database.localSchoolDao().insertLocalSchool(localSchool.copy(dbn = "3"))

        val loaded = database.localSchoolDao().getLocalSchools()
        assertThat(loaded.size).isEqualTo(3)
        assertThat(loaded).contains(localSchool)
        assertThat(loaded).contains(localSchool.copy(dbn = "2"))
        assertThat(loaded).contains(localSchool.copy(dbn = "3"))
    }

    @Test
    fun `should update cyclecount`() = runTest(UnconfinedTestDispatcher()) {
        database.localSchoolDao().insertLocalSchool(localSchool)
        val updatedSchool = localSchool.copy(phone_number = "007")
        database.localSchoolDao().insertLocalSchool(updatedSchool)
        val loaded = database.localSchoolDao().getLocalSchools()
        assertThat(loaded[0]).isEqualTo(updatedSchool)
    }


}
