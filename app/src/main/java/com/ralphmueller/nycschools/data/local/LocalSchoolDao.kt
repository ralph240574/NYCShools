package com.ralphmueller.nycschools.data.local


import androidx.room.*

@Dao
interface LocalSchoolDao {

    @Query("select * from localschool")
    suspend fun getLocalSchools(): List<LocalSchool>

    @Query("select * from localschool where dbn=:dbn")
    suspend fun getLocalSchool(dbn: String): LocalSchool

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalSchool(localSchool: LocalSchool)

}