package br.com.caiorodri.agenpet.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentSettingsBinding
import br.com.caiorodri.agenpet.settings.SettingsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsManager: SettingsManager

    private val themeOptions by lazy {
        mapOf(
            AppCompatDelegate.MODE_NIGHT_NO to getString(R.string.theme_light),
            AppCompatDelegate.MODE_NIGHT_YES to getString(R.string.theme_dark),
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to getString(R.string.theme_system)
        )
    }

    private val languageOptions by lazy {
        mapOf(
            "pt" to getString(R.string.lang_portuguese),
            "en" to getString(R.string.lang_english),
            "es" to getString(R.string.lang_spanish),
            "de" to getString(R.string.lang_german),
            "fr" to getString(R.string.lang_french),
            "it" to getString(R.string.lang_italian),
            "ja" to getString(R.string.lang_japanese),
            "zh" to getString(R.string.lang_chinese)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsManager = SettingsManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCurrentSettings()
        setupListeners()
    }

    private fun loadCurrentSettings() {
        val currentThemeMode = settingsManager.getThemeMode()
        binding.labelThemeCurrentValue.text = themeOptions[currentThemeMode]

        val currentLang = settingsManager.getLanguage()
        binding.labelLanguageCurrentValue.text = languageOptions[currentLang]

        binding.switchNotifications.isChecked = settingsManager.areNotificationsEnabled()
    }

    private fun setupListeners() {
        binding.rowTheme.setOnClickListener {
            showThemeDialog()
        }

        binding.rowLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setNotificationsEnabled(isChecked)
        }
    }

    private fun showThemeDialog() {

        val currentThemeMode = settingsManager.getThemeMode()
        val themeNames = themeOptions.values.toTypedArray()
        val themeValues = themeOptions.keys.toIntArray()

        var checkedItem = themeValues.indexOf(currentThemeMode)

        if (checkedItem == -1) {

            checkedItem = 2

        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.label_modo_escuro)
            .setSingleChoiceItems(themeNames, checkedItem) { dialog, which ->
                val selectedMode = themeValues[which]

                settingsManager.setThemeMode(selectedMode)

                AppCompatDelegate.setDefaultNightMode(selectedMode)

                binding.labelThemeCurrentValue.text = themeNames[which]

                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_button_cancelar, null)
            .show()
    }

    private fun showLanguageDialog() {

        val currentLang = settingsManager.getLanguage()
        val langNames = languageOptions.values.toTypedArray()
        val langValues = languageOptions.keys.toTypedArray()

        var checkedItem = langValues.indexOf(currentLang)

        if (checkedItem == -1) {

            checkedItem = 0

        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.label_idioma_titulo)
            .setSingleChoiceItems(langNames, checkedItem) { dialog, which ->
                val selectedLangCode = langValues[which]

                settingsManager.setLanguage(selectedLangCode)

                settingsManager.applyLanguage()

                binding.labelLanguageCurrentValue.text = langNames[which]

                dialog.dismiss()

                activity?.recreate()
            }
            .setNegativeButton(R.string.dialog_button_cancelar, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}