package com.tu.health.data.repository

import com.tu.health.data.local.ProfileDataStore
import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.AuthAPI
import com.tu.health.data.remote.dto.request.ChangePasswordRequest
import com.tu.health.data.remote.dto.request.LoginRequest
import com.tu.health.data.remote.dto.request.LogoutRequest
import com.tu.health.data.remote.dto.request.RefreshTokenRequest
import com.tu.health.data.remote.dto.request.RegisterRequest
import com.tu.health.data.remote.dto.response.DetailResponse
import com.tu.health.data.remote.dto.response.GetResponse
import com.tu.health.data.remote.dto.response.LoginResponse
import com.tu.health.data.remote.dto.response.RegisterResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthAPI,
    private val secureTokenStore: SecureTokenStore,
    private val profileDataStore: ProfileDataStore
) {
    val accessTokenFlow = secureTokenStore.accessToken
    val refreshTokenFlow = secureTokenStore.refreshToken

    suspend fun registerUser(
        email: String,
        password: String,
        repeatPassword: String,
        firstName: String,
        lastName: String?,
        gender: String,
        birthDate: String
    ): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                password2 = repeatPassword,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                birthDate = birthDate
            )
            val response = api.register(request)

            profileDataStore.saveEmail(email)
            profileDataStore.saveFirstName(firstName)
            profileDataStore.saveLastName(lastName ?: "")

            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()

            val rawMessage = try {
                val json = JSONObject(errorBody ?: "{}")

                when {
                    json.has("email") ->
                        json.getJSONArray("email").getString(0)

                    json.has("password") ->
                        json.getJSONArray("password").getString(0)

                    else -> "Registration failed"
                }
            } catch (_: Exception) {
                "Registration failed"
            }

            val message = rawMessage
                .replace("custom user", "User", ignoreCase = true)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val loginResponse = api.login(LoginRequest(email, password))

            secureTokenStore.saveAccessToken(loginResponse.access)
            secureTokenStore.saveRefreshToken(loginResponse.refresh)
            profileDataStore.saveEmail(email)
            Result.success(loginResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun logout(): Result<DetailResponse> {
        return try {
            val request = LogoutRequest(refreshToken = refreshTokenFlow.first() ?: "")
            val response = api.logout("Bearer ${accessTokenFlow.first()}", request)

            profileDataStore.clear()
            secureTokenStore.clear()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(): Boolean {
        val refreshToken = secureTokenStore.refreshToken.firstOrNull() ?: return false
        return try {
            val response = api.refreshToken(
                RefreshTokenRequest(refreshToken = refreshToken)
            )
            secureTokenStore.saveAccessToken(response.accessToken)
            secureTokenStore.saveRefreshToken(response.refreshToken)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUser(): Result<GetResponse> {
        return try {
            val response = api.get("Bearer ${accessTokenFlow.first()}")

            profileDataStore.saveFirstName(response.firstName)
            profileDataStore.saveLastName(response.lastName ?: "")

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<DetailResponse> {
        return try {
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            val response = api.changePassword(
                "Bearer ${accessTokenFlow.first()}", request
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(): Result<DetailResponse> {
        return try {
            val response = api.delete("Bearer ${accessTokenFlow.first()}")

            profileDataStore.clear()
            secureTokenStore.clear()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
