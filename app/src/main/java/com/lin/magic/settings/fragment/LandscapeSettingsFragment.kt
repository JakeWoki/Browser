package com.lin.magic.settings.fragment

import com.lin.magic.R
import com.lin.magic.extensions.landscapeSharedPreferencesName
import com.lin.magic.settings.preferences.ConfigurationPreferences
import com.lin.magic.settings.preferences.LandscapePreferences
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Landscape settings configuration screen.
 * Notably use the correct shared preferences file rather than the default one.
 */
@AndroidEntryPoint
class LandscapeSettingsFragment : ConfigurationSettingsFragment() {

    @Inject internal lateinit var landscapePreferences: LandscapePreferences

    override fun providePreferencesXmlResource() = R.xml.preference_configuration
    override fun configurationPreferences() : ConfigurationPreferences = landscapePreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //injector.inject(this)
        // That's the earliest place we can change our preference file as earlier in onCreate the manager has not been created yet
        preferenceManager.sharedPreferencesName = requireContext().landscapeSharedPreferencesName()
        preferenceManager.sharedPreferencesMode = MODE_PRIVATE
        super.onCreatePreferences(savedInstanceState,rootKey)

        // For access through options we show which configuration is currently loaded
        findPreference<Preference>(getString(R.string.pref_key_back))?.summary = getString(R.string.settings_title_landscape)
    }

    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.settings_title_landscape
    }
}
