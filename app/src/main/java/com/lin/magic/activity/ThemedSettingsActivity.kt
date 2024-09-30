package com.lin.magic.activity

import com.lin.magic.R
import com.lin.magic.extensions.isDarkTheme
import com.lin.magic.extensions.setStatusBarIconsColor


abstract class ThemedSettingsActivity : ThemedActivity() {

    override fun onResume() {
        super.onResume()
        // Make sure icons have the right color
        //window.setStatusBarIconsColor(foregroundColorFromBackgroundColor(ThemeUtils.getPrimaryColor(this)) == Color.BLACK && !userPreferences.useBlackStatusBar)
        window.setStatusBarIconsColor(!(isDarkTheme() || userPreferences.useBlackStatusBar))
        resetPreferences()
        if (userPreferences.useTheme != themeId) {
            recreate()
        }

        if (userPreferences.useAccent != accentId) {
            recreate()
        }
    }
    
    override fun accentStyle(accentTheme: com.lin.magic.AccentTheme): Int? {
        return when (accentTheme) {
            com.lin.magic.AccentTheme.DEFAULT_ACCENT -> null
            com.lin.magic.AccentTheme.PINK -> R.style.Accent_Pink
            com.lin.magic.AccentTheme.PURPLE ->  R.style.Accent_Puple
            com.lin.magic.AccentTheme.DEEP_PURPLE -> R.style.Accent_Deep_Purple
            com.lin.magic.AccentTheme.INDIGO -> R.style.Accent_Indigo
            com.lin.magic.AccentTheme.BLUE -> R.style.Accent_Blue
            com.lin.magic.AccentTheme.LIGHT_BLUE -> R.style.Accent_Light_Blue
            com.lin.magic.AccentTheme.CYAN -> R.style.Accent_Cyan
            com.lin.magic.AccentTheme.TEAL -> R.style.Accent_Teal
            com.lin.magic.AccentTheme.GREEN -> R.style.Accent_Green
            com.lin.magic.AccentTheme.LIGHT_GREEN -> R.style.Accent_Light_Green
            com.lin.magic.AccentTheme.LIME -> R.style.Accent_Lime
            com.lin.magic.AccentTheme.YELLOW -> R.style.Accent_Yellow
            com.lin.magic.AccentTheme.AMBER -> R.style.Accent_Amber
            com.lin.magic.AccentTheme.ORANGE -> R.style.Accent_Orange
            com.lin.magic.AccentTheme.DEEP_ORANGE -> R.style.Accent_Deep_Orange
            com.lin.magic.AccentTheme.BROWN -> R.style.Accent_Brown
        }
    }
}
