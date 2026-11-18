package com.example.lab1.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.IOException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

interface ChallengesApiService {
    @GET("Harambl/5c686822c6f73104339b1c5e082924ea/raw/6cd7cf277ad1dbe9855d1b41080a09741e2ac7e0/challenges.json")
    suspend fun getChallenges(): ChallengesResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://gist.githubusercontent.com/"

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    val instance: ChallengesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ChallengesApiService::class.java)
    }
}

class ChallengesRepository {
    private val api = RetrofitClient.instance

    suspend fun fetchChallengesWithRetry(maxRetries: Int = 3): NetworkResult<List<Challenge>> {
        var attempts = 0
        while (attempts < maxRetries) {
            try {
                val response = api.getChallenges()
                return NetworkResult.Success(response.challenges)
            } catch (e: IOException) {

                attempts++
                if (attempts >= maxRetries) {
                    return NetworkResult.Error("Нет подключения к интернету", isRetryable = true)
                }
                delay((2000L * attempts).milliseconds)
            } catch (e: HttpException) {
                return NetworkResult.Error("Ошибка сервера: ${e.code()}", isRetryable = true)
            } catch (e: Exception) {
                return NetworkResult.Error("Неизвестная ошибка: ${e.message}", isRetryable = false)
            }
        }
        return NetworkResult.Error("Превышено количество попыток", isRetryable = true)
    }
}

sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()
    object Idle : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val isRetryable: Boolean) : NetworkResult<Nothing>()
}