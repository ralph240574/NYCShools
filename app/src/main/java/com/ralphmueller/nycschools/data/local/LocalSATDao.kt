package com.ralphmueller.nycschools.data.local


import androidx.room.*

@Dao
interface LocalSATDao {

    @Query("select * from localsat")
    suspend fun getLocalSATs(): List<LocalSAT>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalSAT(localSAT: LocalSAT)

}