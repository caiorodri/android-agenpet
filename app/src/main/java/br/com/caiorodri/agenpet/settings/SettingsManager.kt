package br.com.caiorodri.agenpet.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "agenpet_settings_prefs"
        private const val KEY_THEME = "key_theme_mode"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_NOTIFICATIONS = "key_notifications_enabled"
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME, mode).apply()
    }

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun applyTheme() {
        val mode = getThemeMode()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun applyLanguage() {
        val languageCode = getLanguage()
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}