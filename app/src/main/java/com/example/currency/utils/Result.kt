package com.example.currency.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    object Loading : Result<Nothing>()

    data class Error(val message: String?, val statusCode: Int? = null) : Result<Nothing>()

    data class Success<out T>(val data: T) : Result<T>()
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading) }
        .catch {
            when (it) {
                is IOException -> {
                    emit(Result.Error(it.localizedMessage))
                }

                is HttpException -> {
                    emit(Result.Error(it.message))
                }
            }
        }
}
