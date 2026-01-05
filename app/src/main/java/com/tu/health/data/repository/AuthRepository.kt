package com.tu.health.data.repository

import com.tu.health.data.local.ProfileDataStore
import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.api.AuthAPI
import com.tu.health.data.remote.dto.AccountDTO
import com.tu.health.data.remote.dto.DetailDTO
import com.tu.health.data.remote.dto.TokensDTO
import com.tu.health.data.remote.request.ChangePasswordRequest
import com.tu.health.data.remote.request.LoginRequest
import com.tu.health.data.remote.request.LogoutRequest
import com.tu.health.data.remote.request.RegisterRequest
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthAPI,
    private val secureTokenStore: SecureTokenStore,
    private val profileDataStore: ProfileDataStore
) {
    val refreshTokenFlow = secureTokenStore.refreshToken

    suspend fun registerUser(
        email: String,
        password: String,
        repeatPassword: String,
        firstName: String,
        lastName: String?,
        gender: String,
        birthDate: String
    ): Result<AccountDTO> {
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
                    json.has("email") -> json.getJSONArray("email").getString(0)
                    json.has("password") -> json.getJSONArray("password").getString(0)
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

    suspend fun loginUser(email: String, password: String): Result<TokensDTO> =
        safeCall {
            val loginResponse = api.login(LoginRequest(email, password))
            secureTokenStore.saveAccessToken(loginResponse.accessToken)
            secureTokenStore.saveRefreshToken(loginResponse.refreshToken)
            profileDataStore.saveEmail(email)
            loginResponse
        }

    suspend fun logout(): Result<DetailDTO> =
        safeCall {
            val request = LogoutRequest(refreshToken = refreshTokenFlow.first() ?: "")
            val response = api.logout(request)

            profileDataStore.clear()
            secureTokenStore.clear()

            response
        }

    suspend fun getUser(): Result<AccountDTO> =
        safeCall {
            val response = api.get()
            profileDataStore.saveFirstName(response.firstName)
            profileDataStore.saveLastName(response.lastName ?: "")
            response
        }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<DetailDTO> =
        safeCall {
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            api.changePassword(request)
        }

    suspend fun delete(): Result<DetailDTO> =
        safeCall {
            val response = api.delete()

            profileDataStore.clear()
            secureTokenStore.clear()

            response
        }
}
