package com.lin.magic.settings.fragment

import com.lin.magic.app
import com.lin.magic.R
import com.lin.magic.extensions.find
import com.lin.magic.settings.preferences.DomainPreferences
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import slions.pref.BasicPreference


@AndroidEntryPoint
class DomainSettingsFragment : AbstractSettingsFragment() {

    lateinit var prefs: DomainPreferences

    // Capture that as it could change as we open parent settings
    val domain = app.domain

    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        //We use a dynamic string instead, see below
        return -1
    }

    /**
     * See [AbstractSettingsFragment.title]
     */
    override fun title(): String {
        return domain
    }

    /**
     * Select our layout depending if we are showing our default domain settings or not
     */
    override fun providePreferencesXmlResource() = if (domain=="") R.xml.preference_domain_default else R.xml.preference_domain

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        prefs = DomainPreferences(requireContext(),domain)
        // That's the earliest place we can change our preference file as earlier in onCreate the manager has not been created yet
        preferenceManager.sharedPreferencesName = DomainPreferences.name(domain)
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
        super.onCreatePreferences(savedInstanceState, rootKey)

        // Setup link and domain display
        find<Preference>(R.string.pref_key_visit_domain)?.apply {
            summary = domain
            val uri = "http://" + domain
            intent?.data = Uri.parse(uri)
        }

        // Setup parent link
        find<BasicPreference>(R.string.pref_key_parent)?.apply {
            if (prefs.isSubDomain) {
                breadcrumb = prefs.parent?.domain ?: ""
                summary = breadcrumb
            } else {
                breadcrumb = getString(R.string.settings_summary_default_domain_settings)
                summary = breadcrumb
            }

            setOnPreferenceClickListener {
                // Set domain setting page to load
                app.domain = prefs.parent?.domain ?: ""
                // Still perform default action
                false
            }
        }


        // Delete this domain settings
        find<Preference>(R.string.pref_key_delete)?.setOnPreferenceClickListener {
            DomainPreferences.delete(domain)
            parentFragmentManager.popBackStack()
            true
        }
    }
}
