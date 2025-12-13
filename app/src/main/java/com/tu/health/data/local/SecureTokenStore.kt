package com.tu.health.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "secure_prefs"
private const val KEYSTORE_ALIAS = "health_app_key_alias"
private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
private const val ACCESS_TOKEN_IV = "ACCESS_TOKEN_IV"
private const val REFRESH_TOKEN_IV = "REFRESH_TOKEN_IV"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

@Singleton
class SecureTokenStore @Inject constructor(@param:ApplicationContext private val context: Context) {

    private val secretKey: SecretKey by lazy { createOrGetSecretKey() }

    private fun createOrGetSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        // Return existing key if any
        if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            return (keyStore.getEntry(KEYSTORE_ALIAS, null)
                    as KeyStore.SecretKeyEntry).secretKey
        }

        // Else generate a new AES key
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    suspend fun saveAccessToken(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher
            .doFinal(token.toByteArray(Charset.forName("UTF-8")))

        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(ACCESS_TOKEN_KEY)] = Base64
                .encodeToString(encrypted, Base64.DEFAULT)
            prefs[stringPreferencesKey(ACCESS_TOKEN_IV)] = Base64
                .encodeToString(iv, Base64.DEFAULT)
        }
    }

    suspend fun saveRefreshToken(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(token
            .toByteArray(Charset.forName("UTF-8"))
        )

        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(REFRESH_TOKEN_KEY)] = Base64
                .encodeToString(encrypted, Base64.DEFAULT)
            prefs[stringPreferencesKey(REFRESH_TOKEN_IV)] = Base64
                .encodeToString(iv, Base64.DEFAULT)
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        val encrypted = prefs[stringPreferencesKey(ACCESS_TOKEN_KEY)] ?: return@map null
        val ivEncoded = prefs[stringPreferencesKey(ACCESS_TOKEN_IV)] ?: return@map null
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = Base64.decode(ivEncoded, Base64.DEFAULT)
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                GCMParameterSpec(128, iv)
            )
            val decoded = cipher
                .doFinal(Base64.decode(encrypted, Base64.DEFAULT))
            String(decoded, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        val encrypted = prefs[stringPreferencesKey(REFRESH_TOKEN_KEY)] ?: return@map null
        val ivEncoded = prefs[stringPreferencesKey(REFRESH_TOKEN_IV)] ?: return@map null
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = Base64.decode(ivEncoded, Base64.DEFAULT)
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                GCMParameterSpec(128, iv))
            val decoded = cipher.doFinal(Base64
                .decode(encrypted, Base64.DEFAULT))
            String(decoded, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
