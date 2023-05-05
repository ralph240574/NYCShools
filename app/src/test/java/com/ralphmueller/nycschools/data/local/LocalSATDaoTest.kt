package com.ralphmueller.nycschools.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.ralphmueller.nycschools.utils.localSAT
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
class LocalSATDaoTest {

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
    fun `should insert sat`() = runTest(UnconfinedTestDispatcher()) {
        database.localSATDao().insertLocalSAT(localSAT)
        val loaded = database.localSATDao().getLocalSATs()
        assertThat(loaded[0]).isEqualTo(localSAT)
    }

    @Test
    fun `should insert multiple sats`() = runTest(UnconfinedTestDispatcher()) {
        database.localSATDao().insertLocalSAT(localSAT)
        database.localSATDao().insertLocalSAT(localSAT.copy(dbn = "2"))
        database.localSATDao().insertLocalSAT(localSAT.copy(dbn = "3"))

        val loaded = database.localSATDao().getLocalSATs()
        assertThat(loaded.size).isEqualTo(3)
        assertThat(loaded).contains(localSAT)
        assertThat(loaded).contains(localSAT.copy(dbn = "2"))
        assertThat(loaded).contains(localSAT.copy(dbn = "3"))
    }

    @Test
    fun `should update sat`() = runTest(UnconfinedTestDispatcher()) {
        database.localSATDao().insertLocalSAT(localSAT)
        val updatedSAT = localSAT.copy(school_name = "007")
        database.localSATDao().insertLocalSAT(updatedSAT)
        val loaded = database.localSATDao().getLocalSATs()
        assertThat(loaded[0]).isEqualTo(updatedSAT)
    }

}
