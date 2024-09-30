package com.lin.magic.settings.preferences

import com.lin.magic.constant.PrefKeys
import com.lin.magic.device.ScreenSize
import com.lin.magic.di.PrefsPortrait
import android.content.SharedPreferences
import com.lin.magic.enums.CutoutMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provide access to portrait specific preferences.
 */
@Singleton
 class PortraitPreferences @Inject constructor(
    @PrefsPortrait preferences: SharedPreferences,
    screenSize: ScreenSize
) : ConfigurationPreferences(preferences, screenSize) {

    // Looks like this is the right way to go


    override fun getDefaultBoolean(aKey: String) : Boolean {
        return iDefaults[aKey] as Boolean
    }

    override fun getDefaultInteger(aKey: String) : Int {
        return iDefaults[aKey] as Int
    }

    override fun getDefaultFloat(aKey: String) : Float {
        return LandscapePreferences.iDefaults[aKey] as Float
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
            PrefKeys.HideStatusBar to false,
            PrefKeys.HideToolBar to false,
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

