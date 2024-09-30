package com.lin.magic.settings.fragment

import com.lin.magic.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference

/**
 * Sponsorship settings for non Google Play Store variants.
 * We just redirect users to Google Play Store if they want to sponsor us.
 */
abstract class RedirectSponsorshipSettingsFragment : AbstractSettingsFragment() {

    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.settings_contribute
    }

    override fun providePreferencesXmlResource() = R.xml.preference_sponsorship

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        addCategoryContribute()
        addPreferenceLinkToGooglePlayStoreFiveStarsReview()
        addPreferenceShareLink()
        addPreferenceLinkToCrowdin()
        addPreferenceLinkToGitHubSponsor()
        addPreferenceLinkToGooglePlayStore()
    }

    /**
     * Add a preference that opens up our play store page.
     */
    private fun addPreferenceLinkToGooglePlayStore() {
        // We invite user to install our Google Play Store release
        val pref = Preference(requireContext())
        pref.isSingleLineTitle = false
        pref.title = resources.getString(R.string.pref_title_no_sponsorship)
        pref.summary = resources.getString(R.string.pref_summary_no_sponsorship)
        pref.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_google_play, activity?.theme)
        pref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            // Open up Magic play store page
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=net.slions.magic.full.playstore"))
            i.setPackage(requireActivity().packageName)
            startActivity(i)
            true
        }
        preferenceScreen.addPreference(pref)
    }






}
