package com.ralphmueller.nycschools.data.local


import androidx.room.*

@Dao
interface LocalSchoolDao {

    @Query("select * from localschool")
    suspend fun getLocalSchools(): List<LocalSchool>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalSchool(localSchool: LocalSchool)

}