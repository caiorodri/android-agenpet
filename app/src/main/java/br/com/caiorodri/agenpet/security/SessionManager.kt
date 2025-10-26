package br.com.caiorodri.agenpet.security

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.commit();
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null);
    }

    fun clearAuthToken() {
        val editor = prefs.edit();
        editor.remove(AUTH_TOKEN);
        editor.commit();

    }
}