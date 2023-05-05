package com.ralphmueller.nycschools.data

sealed class Result<out Any> {

    data class Success<out T>(
        val data: T,
        val message: String? = null
    ) : Result<T>()

    data class Error(val exception: Throwable) : Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading -> "Loading"
            is Success<*> -> "Success[data=$data, message=$message]"
            is Error -> "Error[exception=$exception]"
        }
    }

}
