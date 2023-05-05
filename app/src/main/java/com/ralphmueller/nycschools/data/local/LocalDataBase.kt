package com.ralphmueller.nycschools.data.local


import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        LocalSchool::class,
        LocalSAT::class],
    version = 1,
    exportSchema = false
)

abstract class LocalDataBase : RoomDatabase() {

    abstract fun localSchoolDao(): LocalSchoolDao

    abstract fun localSATDao(): LocalSATDao

}