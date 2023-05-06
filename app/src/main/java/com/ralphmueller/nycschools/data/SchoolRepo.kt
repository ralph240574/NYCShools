package com.ralphmueller.nycschools.data

import com.ralphmueller.nycschools.data.local.NYCSchoolsLocalDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteDataSource
import com.ralphmueller.nycschools.model.School
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SchoolRepo(
    val localDataSource: NYCSchoolsLocalDataSource,
    val remoteDataSource: NYCSchoolsRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getSchools(forceRefresh: Boolean = false): Flow<Result<List<School>>> =

        flow {
            emit(Result.Loading)

            var noData = true
            when (val schools = localDataSource.getSchools()) {
                is Result.Error -> throw schools.exception
                is Result.Success -> {
                    if (schools.data.isNotEmpty()) {
                        noData = false
                    }
                    emit(Result.Success(schools.data))
                }

                else -> {}
            }

            var dataChanged = false
            if (forceRefresh || noData) {
                when (val schools = remoteDataSource.getNycSchools()) {
                    is Result.Error -> throw schools.exception
                    is Result.Success -> {
                        schools.data.forEach {
                            localDataSource.insertLocalSchool(it.toLocalSchool())
                            dataChanged = true
                        }
                    }

                    else -> {}
                }
                when (val sats = remoteDataSource.getNycSats()) {
                    is Result.Error -> throw sats.exception
                    is Result.Success -> {
                        sats.data.forEach {
                            localDataSource.insertLocalSAT(it.toLocalSAT())
                            dataChanged = true
                        }
                    }

                    else -> {}
                }
            }

            if (dataChanged) {
                when (val schools = localDataSource.getSchools()) {
                    is Result.Error -> throw schools.exception
                    is Result.Success -> {
                        if (schools.data.isNotEmpty()) {
                            emit(Result.Success(schools.data))
                        }
                    }

                    else -> {}
                }
            }

        }.flowOn(ioDispatcher)
            .catch { exception ->
                emit(Result.Error(exception))
            }

    suspend fun getSchool(dbn: String): Flow<Result<School>> =

        flow {
            emit(Result.Loading)

            when (val school = localDataSource.getSchool(dbn = dbn)) {
                is Result.Error -> throw school.exception
                is Result.Success -> {
                    emit(Result.Success(school.data))
                }

                else -> {}
            }
        }.flowOn(ioDispatcher)
            .catch { exception ->
                emit(Result.Error(exception))
            }


    fun closeDb() {
        localDataSource.database.close()
    }

    fun clearDb() {
        localDataSource.database.clearAllTables()
    }
}