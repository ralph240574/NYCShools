package com.ralphmueller.nycschools.data.local


import com.ralphmueller.nycschools.data.Result
import com.ralphmueller.nycschools.model.School


class NYCSchoolsLocalDataSource(val database: LocalDataBase) {

    suspend fun getLocalSchools(): Result<List<LocalSchool>> {
        val data = database.localSchoolDao().getLocalSchools()
        return Result.Success(data = data)
    }

    suspend fun insertLocalSchool(localSchool: LocalSchool): Result<Boolean> {
        database.localSchoolDao().insertLocalSchool(localSchool = localSchool)
        return Result.Success(data = true)
    }

    suspend fun getLocalSATs(): Result<List<LocalSAT>> {
        val data = database.localSATDao().getLocalSATs()
        return Result.Success(data = data)
    }

    suspend fun insertLocalSAT(localSAT: LocalSAT): Result<Boolean> {
        database.localSATDao().insertLocalSAT(localSAT = localSAT)
        return Result.Success(data = true)
    }


    suspend fun getSchools(): Result<List<School>> {
        val localSchools = database.localSchoolDao().getLocalSchools()
        val satsMap = database.localSATDao().getLocalSATs().associate { Pair(it.dbn, it) }
        val schools = localSchools.map { it.toSchool(satsMap.get(it.dbn)) }
        return Result.Success(data = schools)
    }

    suspend fun getSchool(dbn: String): Result<School> {
        val localSchool = database.localSchoolDao().getLocalSchool(dbn = dbn)
        val sat = database.localSATDao().getLocalSAT(dbn = dbn)
        val school = localSchool.toSchool(sat)
        return Result.Success(data = school)

    }

}