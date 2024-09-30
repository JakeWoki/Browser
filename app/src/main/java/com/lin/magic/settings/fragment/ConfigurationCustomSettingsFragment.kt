package com.lin.magic.settings.fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import com.lin.magic.R
import com.lin.magic.app
import com.lin.magic.device.ScreenSize
import com.lin.magic.extensions.resizeAndShow
import com.lin.magic.settings.preferences.ConfigurationCustomPreferences
import com.lin.magic.settings.preferences.ConfigurationPreferences
import timber.log.Timber

/**
 * Custom configuration settings configuration screen.
 * Notably use the correct shared preferences file rather than the default one.
 */
@AndroidEntryPoint
class ConfigurationCustomSettingsFragment : ConfigurationSettingsFragment() {

    internal lateinit var preferences: ConfigurationCustomPreferences

    override fun providePreferencesXmlResource() = R.xml.preference_configuration
    override fun configurationPreferences() : ConfigurationPreferences = preferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //injector.inject(this)
        // That's the earliest place we can change our preference file as earlier in onCreate the manager has not been created yet
        preferenceManager.sharedPreferencesName = app.config.fileName
        preferenceManager.sharedPreferencesMode = MODE_PRIVATE
        preferences = ConfigurationCustomPreferences(preferenceManager.sharedPreferences!!, ScreenSize(requireContext()))
        super.onCreatePreferences(savedInstanceState,rootKey)

        // For access through options we show which configuration is currently loaded
        findPreference<Preference>(getString(R.string.pref_key_back))?.summary = app.config.name(requireContext())

        findPreference<Preference>(getString(R.string.pref_key_delete))?.isVisible = true

        clickableDynamicPreference(
            preference = getString(R.string.pref_key_delete),
            summary = app.config.name(requireContext())
        ) {
            var cancel = false

            // See: https://stackoverflow.com/a/10358260/3969362
            // Make a handler that throws a runtime exception when a message is received
            // That allows us to exit our nested loop
            // TODO: Make that nested loop thingy pretty using extensions?
            val handler = Handler(Looper.myLooper()!!) {
                throw RuntimeException()
            }

            MaterialAlertDialogBuilder(requireContext())
                .setCancelable(true)
                .setTitle(R.string.session_prompt_confirm_deletion_title)
                .setMessage(getString(R.string.configuration_prompt_confirm_deletion_message, app.config.name(requireContext())))
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    cancel = true
                    // Exit our nested event loop
                    handler.sendMessage(handler.obtainMessage())
                }.setOnCancelListener {
                    cancel = true
                    // Exit our nested event loop
                    handler.sendMessage(handler.obtainMessage())
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    // Delete our config
                    app.config.delete()
                    // Exit our nested event loop
                    handler.sendMessage(handler.obtainMessage());
                }
                .resizeAndShow()

            // Start nested event loop as we don't want to return before user resolves above dialog
            try {
                Looper.loop()
            } catch (ex: RuntimeException) {
                // We need to make sure the dialog can't be dismissed without exiting our nested loop as we don't want to accumulate them
                Timber.d("Loop exited")
            }

            // Returning true will cancel our default action which is to go back
            // Returning false after deleting this configuration will go back to previous setting page
            cancel
        }

    }

    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.settings_title_landscape
    }

    /**
     * See [AbstractSettingsFragment.title]
     */
    override fun title(): String {
        return app.config.name(requireContext())
    }

}
