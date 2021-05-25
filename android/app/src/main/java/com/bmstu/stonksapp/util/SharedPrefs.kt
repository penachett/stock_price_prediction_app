package com.bmstu.stonksapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class SharedPrefs(context: Context) {
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    init {
        try {
            val key: MasterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "create ESP exception: ${e.message}")
        }
    }

    fun havePrefs(): Boolean {
        return prefs.contains(LOGIN_KEY)
    }

    fun saveAuthData(login: String?, password: String?) {
        editor = prefs.edit()
        editor.putString(LOGIN_KEY, login)
        editor.putString(PASSWORD_KEY, password)
        editor.apply()
    }

    val login: String?
        get() = prefs.getString(LOGIN_KEY, "")

    val password: String?
        get() = prefs.getString(PASSWORD_KEY, "")

    val isAutoAuthEnabled: Boolean
        get() = prefs.getBoolean(AUTO_AUTH_KEY, false)

    fun setAutoAuth(enableAutoAuth: Boolean) {
        editor = prefs.edit()
        editor.putBoolean(AUTO_AUTH_KEY, enableAutoAuth)
        editor.apply()
    }

    fun haveAuthData(): Boolean {
        return !(prefs.getString(LOGIN_KEY, "") == "" || prefs.getString(PASSWORD_KEY, "") == "")
    }

    companion object {
        private const val TAG = "Shared Prefs"
        private const val PREFS_NAME = "encrypted_prefs"
        private const val PASSWORD_KEY = "prefs_password"
        private const val LOGIN_KEY = "prefs_login"
        private const val AUTO_AUTH_KEY = "auto_auth"
    }
}