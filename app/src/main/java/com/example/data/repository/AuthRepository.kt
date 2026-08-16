package com.example.data.repository

import android.content.Context
import com.example.data.local.AuthDataStore
import com.example.data.model.*
import com.example.data.remote.AuthApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AuthRepository(context: Context) {
    private val authDataStore = AuthDataStore(context)
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // Emulator localhost
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(AuthApi::class.java)

    suspend fun register(request: UserRegistrationRequest): Result<AuthResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    authDataStore.saveToken(it.token)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    authDataStore.saveToken(it.token)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<User> {
        val token = authDataStore.tokenFlow.first() ?: return Result.failure(Exception("Not logged in"))
        return try {
            val response = api.getMe("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Failed to fetch user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        val token = authDataStore.tokenFlow.first()
        if (token != null) {
            try { api.logout("Bearer $token") } catch (e: Exception) { /* Ignore */ }
        }
        authDataStore.clearToken()
    }

    suspend fun isLoggedIn(): Boolean {
        return authDataStore.tokenFlow.first() != null
    }

    suspend fun forgotPassword(email: String): Result<SuccessResponse> {
        return try {
            val response = api.forgotPassword(mapOf("email" to email))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
