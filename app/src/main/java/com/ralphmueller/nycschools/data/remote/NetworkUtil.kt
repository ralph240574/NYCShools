package com.ralphmueller.nycschools.data.remote

import com.ralphmueller.nycschools.data.Result

import java.io.IOException

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        Result.Error(IOException(e.message, e))
    }
}
