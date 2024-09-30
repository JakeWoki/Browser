package com.lin.magic.settings.fragment

import com.lin.magic.R
import com.lin.magic.settings.preferences.UserPreferences
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Scrollbars settings screen.
 */
@AndroidEntryPoint
class ScrollbarsSettingsFragment : AbstractSettingsFragment() {

    @Inject internal lateinit var userPreferences: UserPreferences

    override fun providePreferencesXmlResource() = R.xml.preference_scrollbars

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //injector.inject(this)
        // That's the earliest place we can change our preference file as earlier in onCreate the manager has not been created yet
        //preferenceManager.sharedPreferencesName = requireContext().portraitSharedPreferencesName()
        //preferenceManager.sharedPreferencesMode = MODE_PRIVATE
        super.onCreatePreferences(savedInstanceState,rootKey)
    }

    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.pref_category_scrollbars
    }
}
