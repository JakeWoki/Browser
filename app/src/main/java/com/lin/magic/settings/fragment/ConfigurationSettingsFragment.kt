package com.lin.magic.settings.fragment

import com.lin.magic.R
import com.lin.magic.constant.PrefKeys
import com.lin.magic.device.ScreenSize
import com.lin.magic.settings.preferences.ConfigurationPreferences
import com.lin.magic.settings.preferences.UserPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.TwoStatePreference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Configuration settings abstract base class
 */
@AndroidEntryPoint
abstract class ConfigurationSettingsFragment : AbstractSettingsFragment() {

    @Inject internal lateinit var userPreferences: UserPreferences
    @Inject internal lateinit var screenSize: ScreenSize

    override fun providePreferencesXmlResource() = R.xml.preference_configuration

    abstract fun configurationPreferences() : ConfigurationPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        //injector.inject(this)
        super.onCreate(savedInstanceState)


        /*
        // Sample to loop through our preference store
        preferenceManager.sharedPreferences.all.forEach {
            findPreference<Preference>(it.key)
        }
        */

        /*
        // For each preference element on our screen
        val preferenceCount: Int = preferenceManager.preferenceScreen.preferenceCount
        for (i in 0 until preferenceCount) {
            val preference: Preference = preferenceManager.preferenceScreen.getPreference(i)
            val curKey = preference.key
            //if (preference.setDefaultValue())
        }
        */

    }

    /**
     * Reflect actual default in our UI unless user already changed that preference.
     * Needed because we can not rely on XML defaults for configurations since they are instantiated.
     * [aKey] is our preference key.
     * [aValue] is our preference default value.
     */
    fun setDefaultIfNeeded(aKey: String, aValue: Any) {
        if (preferenceManager.sharedPreferences!!.contains(aKey)) {
            // User defined to settings option, no need to initialize it's default then
            Timber.d("User defined: $aKey")
        } else {
            // There is no user defined value for this preference therefore we need to set it's value to the default one
            // TODO: extend this if we need to support new kind of preferences or value types
            if (aValue is Boolean) {
                findPreference<TwoStatePreference>(aKey)?.isChecked = preferenceManager!!.sharedPreferences!!.getBoolean(aKey,aValue)
            } else if (aValue is Int) {
                findPreference<SeekBarPreference>(aKey)?.value = preferenceManager!!.sharedPreferences!!.getInt(aKey,aValue)
            }
            // That is useless at this stage probably only used during construction
            //findPreference<Preference>(it.key)?.setDefaultValue(aValue)
        }

    }

    /**
     * Called from [PreferenceFragmentCompat.onCreate]
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        // At this stage our preferences have been created
        // Go through our defaults and set them as needed
        configurationPreferences().getDefaults().forEach {
            setDefaultIfNeeded(it.key,it.value)
        }

        // Handle special case for vertical tab default which is only known at runtime
        setDefaultIfNeeded(PrefKeys.TabBarVertical,!screenSize.isTablet())
        setDefaultIfNeeded(PrefKeys.TabBarInDrawer,!screenSize.isTablet())

    }

}
