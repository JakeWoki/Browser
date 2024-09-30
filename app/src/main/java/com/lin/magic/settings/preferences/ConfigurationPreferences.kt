package com.lin.magic.settings.preferences

import com.lin.magic.R
import com.lin.magic.constant.PrefKeys
import com.lin.magic.device.ScreenSize
import android.content.SharedPreferences
import com.lin.magic.settings.preferences.delegates.booleanPreference
import com.lin.magic.settings.preferences.delegates.enumPreference
import com.lin.magic.settings.preferences.delegates.floatPreference

/**
 * Base class that provides access to configuration specific preferences.
 * Derived class notably includes Portrait and Landscape variants.
 */
abstract class ConfigurationPreferences constructor(
    val preferences: SharedPreferences,
    screenSize: ScreenSize
) : ConfigurationDefaults {

    /**
     * True if the system status bar should be hidden throughout the app, false if it should be
     * visible.
     */
    var hideStatusBar by preferences.booleanPreference(R.string.pref_key_hide_status_bar, getDefaultBoolean(PrefKeys.HideStatusBar))

    /**
     * True if the browser should hide the navigation bar when scrolling, false if it should be
     * immobile.
     */
    //@Suppress("CALLING_NONFINAL") //TODO Find a way to suppress that warning
    var hideToolBar by preferences.booleanPreference(R.string.pref_key_hide_tool_bar, getDefaultBoolean(PrefKeys.HideToolBar))

    /**
     */
    var showToolBarOnScrollUp by preferences.booleanPreference(R.string.pref_key_show_tool_bar_on_scroll_up, getDefaultBoolean(PrefKeys.ShowToolBarWhenScrollUp))

    /**
     */
    var showToolBarOnPageTop by preferences.booleanPreference(R.string.pref_key_show_tool_bar_on_page_top, getDefaultBoolean(PrefKeys.ShowToolBarOnPageTop))

    /**
     *
     */
    var pullToRefresh by preferences.booleanPreference(R.string.pref_key_pull_to_refresh, getDefaultBoolean(PrefKeys.PullToRefresh))

    /**
     * True if the app should put the tab bar inside a drawer.
     * False will put vertical tab bar beside the tab view.
     */
    var tabBarInDrawer by preferences.booleanPreference(R.string.pref_key_tab_bar_in_drawer, !screenSize.isTablet())

    /**
     * True if the app should use the navigation drawer UI, false if it should use the traditional
     * desktop browser tabs UI.
     */
    var verticalTabBar by preferences.booleanPreference(R.string.pref_key_tab_bar_vertical, !screenSize.isTablet())

    /**
     *
     */
     var toolbarsBottom by preferences.booleanPreference(R.string.pref_key_toolbars_bottom, getDefaultBoolean(PrefKeys.ToolbarsBottom))

    /**
     * Define viewport width for desktop mode. Expressed in percentage of the actual viewport width.
     * When set to 100% we use actual viewport width, the HTML page is not tempered with.
     * When set to something other than 100% we will enable wide viewport mode and inject JS code to set HTML meta viewport element accordingly.
     */
    var desktopWidth by preferences.floatPreference(R.string.pref_key_desktop_width, getDefaultFloat(PrefKeys.DesktopWidth))


    /**
     * Define if we render around display cutouts.
     *
     * See: https://developer.android.com/reference/android/view/WindowManager.LayoutParams
     * See: https://developer.android.com/reference/android/view/WindowManager.LayoutParams#LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
     */
    var cutoutMode by preferences.enumPreference(R.string.pref_key_cutout_mode, getDefaultCutoutMode())

}

