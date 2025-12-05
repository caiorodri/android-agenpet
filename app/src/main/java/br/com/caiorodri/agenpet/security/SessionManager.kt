package br.com.caiorodri.agenpet.security

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

    companion object {
        const val AUTH_TOKEN = "auth_token";
        const val KEY_REMEMBER_ME = "remember_me";
        const val KEY_LAST_SESSION_TIME = "last_session_time";
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.putLong(KEY_LAST_SESSION_TIME, System.currentTimeMillis());
        editor.apply();
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null);
    }

    fun clearAuthToken() {
        val editor = prefs.edit();
        editor.remove(AUTH_TOKEN);
        editor.remove(KEY_LAST_SESSION_TIME);
        editor.apply();

    }

    fun saveRememberMe(remember: Boolean) {
        val editor = prefs.edit();
        editor.putBoolean(KEY_REMEMBER_ME, remember);
        editor.apply();
    }

    fun fetchRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false);
    }

    fun updateLastSessionTime() {
        val editor = prefs.edit();
        editor.putLong(KEY_LAST_SESSION_TIME, System.currentTimeMillis());
        editor.apply();
    }

    fun fetchLastSessionTime(): Long {
        return prefs.getLong(KEY_LAST_SESSION_TIME, 0L);
    }

}