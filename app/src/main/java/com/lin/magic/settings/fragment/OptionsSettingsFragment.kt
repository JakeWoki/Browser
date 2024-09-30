package com.lin.magic.settings.fragment

import com.lin.magic.app
import com.lin.magic.R
import com.lin.magic.extensions.find
import com.lin.magic.extensions.isLandscape
import com.lin.magic.extensions.isPortrait
import com.lin.magic.settings.preferences.DomainPreferences
import com.lin.magic.settings.preferences.UserPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import com.lin.magic.di.configPrefs
import com.lin.magic.extensions.configId
import com.lin.magic.settings.Config
import com.lin.magic.settings.preferences.ConfigurationCustomPreferences
import javax.inject.Inject

/**
 * Options settings screen.
 * Typically displayed in a bottom sheet from the browser activity.
 */
@AndroidEntryPoint
class OptionsSettingsFragment : AbstractSettingsFragment() {

    // Capture that as it could change through navigating our domain settings hierarchy
    val domain = app.domain

    @Inject internal lateinit var userPreferences: UserPreferences

    override fun providePreferencesXmlResource() = R.xml.preference_options

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState,rootKey)

        setupConfiguration()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        setupConfiguration()
    }

    /**
     * Only show the configuration options for the current configuration
     */
    private fun setupConfiguration() {
        // TODO: Just setup a preference using our configPrefs instead of dealing with visibility of those three preferences?
        if (requireContext().configPrefs is ConfigurationCustomPreferences) {
            // Tell our configuration settings fragment to open the proper file
            app.config = Config(requireContext().configId)
            findPreference<Preference>(getString(R.string.pref_key_portrait))?.isVisible =  false
            findPreference<Preference>(getString(R.string.pref_key_landscape))?.isVisible =  false
            findPreference<Preference>(getString(R.string.pref_key_configuration_custom))?.apply {
                isVisible = true
                summary = app.config.name(requireContext())
            }
        } else {
            findPreference<Preference>(getString(R.string.pref_key_portrait))?.isVisible =  requireActivity().isPortrait
            findPreference<Preference>(getString(R.string.pref_key_landscape))?.isVisible =  requireActivity().isLandscape
            findPreference<Preference>(getString(R.string.pref_key_configuration_custom))?.isVisible =  false
        }
    }

    /**
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Timber.d("Domain: ${app.domain}")
        // Don't show domain settings if it does not exists yet
        // Most important so that we don't create them when in incognito mode
        find<Preference>(R.string.pref_key_domain)?.apply{
            isVisible = DomainPreferences.exists(domain)
            setOnPreferenceClickListener {
                app.domain = domain
                false
            }
        }

        // Need when coming back from sub menu after rotation for instance
        setupConfiguration()
    }


    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.options
    }
}
