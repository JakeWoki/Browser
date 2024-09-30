package com.lin.magic.settings.preferences

import com.lin.magic.constant.PrefKeys
import com.lin.magic.device.ScreenSize
import android.content.SharedPreferences
import com.lin.magic.enums.CutoutMode

/**
 * Provide access to specific preferences.
 */
//@Singleton
class ConfigurationCustomPreferences constructor(
    preferences: SharedPreferences,
    screenSize: ScreenSize
) : ConfigurationPreferences(preferences, screenSize) {

    override fun getDefaultBoolean(aKey: String) : Boolean {
        return iDefaults[aKey] as Boolean
    }

    override fun getDefaultInteger(aKey: String) : Int {
        return iDefaults[aKey] as Int
    }

    override fun getDefaultFloat(aKey: String) : Float {
        return iDefaults[aKey] as Float
    }

    override fun getDefaultCutoutMode(): CutoutMode {
        return CutoutMode.Default
    }

    override fun getDefaults(): Map<String, Any> {
        return iDefaults
    }

    companion object {
        // Define our defaults
        // Needs to be static as it is accessed by the base class constructor through the virtual functions above
        val iDefaults = mapOf(
            PrefKeys.HideStatusBar to true,
            PrefKeys.HideToolBar to true,
            PrefKeys.ShowToolBarWhenScrollUp to false,
            PrefKeys.ShowToolBarOnPageTop to true,
            PrefKeys.PullToRefresh to true,
            PrefKeys.ToolbarsBottom to false,
            PrefKeys.DesktopWidth to 200F
            // Omitted the following as they have non static default values specified in the base class
            //PrefKeys.TabBarVertical to !screenSize.isTablet(),
            //PrefKeys.TabBarInDrawer to !screenSize.isTablet(),
        )

    }
}

