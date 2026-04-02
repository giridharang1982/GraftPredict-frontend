package com.simats.graftpredict.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SessionManager(context: Context) {
    private val prefsFile = "graftpredict_prefs"
    private val prefs = EncryptedSharedPreferences.create(
        prefsFile,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_FIRST_NAME = "key_first_name"
        private const val KEY_LAST_NAME = "key_last_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_DOB = "key_dob"
        private const val KEY_GENDER = "key_gender"
        private const val KEY_AGE = "key_age"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_USER_ROLE = "key_user_role"
    }

    fun saveSession(token: String, userId: String?, firstName: String?, lastName: String?, email: String?, userRole: String? = null) {
        prefs.edit().putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_USER_ROLE, userRole)
            .apply()
    }

    fun saveUserDetails(
        dob: String?,
        gender: String?,
        age: Int?,
        language: String?,
        userRole: String?
    ) {
        prefs.edit()
            .putString(KEY_DOB, dob)
            .putString(KEY_GENDER, gender)
            .putInt(KEY_AGE, age ?: 0)
            .putString(KEY_LANGUAGE, language)
            .putString(KEY_USER_ROLE, userRole)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getFirstName(): String? = prefs.getString(KEY_FIRST_NAME, null)
    fun getLastName(): String? = prefs.getString(KEY_LAST_NAME, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getDob(): String? = prefs.getString(KEY_DOB, null)
    fun getGender(): String? = prefs.getString(KEY_GENDER, null)
    fun getAge(): Int = prefs.getInt(KEY_AGE, 0)
    fun getLanguage(): String? = prefs.getString(KEY_LANGUAGE, null)
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()
}
