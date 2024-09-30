package com.lin.magic.activity

import com.lin.magic.R
import android.os.Bundle
import com.lin.magic.AccentTheme

//@AndroidEntryPoint
abstract class ThemedBrowserActivity : ThemedActivity() {

    private var shouldRunOnResumeActions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && shouldRunOnResumeActions) {
            shouldRunOnResumeActions = false
            onWindowVisibleToUserAfterResume()
        }
    }

    override fun onResume() {
        super.onResume()
        resetPreferences()
        shouldRunOnResumeActions = true
        if (themeId != userPreferences.useTheme) {
            restart()
        }

        if (accentId != userPreferences.useAccent) {
            restart()
        }
    }

    override fun accentStyle(accentTheme: AccentTheme): Int? {
        return when (accentTheme) {
            AccentTheme.DEFAULT_ACCENT -> null
            AccentTheme.PINK -> R.style.Accent_Pink
            AccentTheme.PURPLE ->  R.style.Accent_Puple
            AccentTheme.DEEP_PURPLE -> R.style.Accent_Deep_Purple
            AccentTheme.INDIGO -> R.style.Accent_Indigo
            AccentTheme.BLUE -> R.style.Accent_Blue
            AccentTheme.LIGHT_BLUE -> R.style.Accent_Light_Blue
            AccentTheme.CYAN -> R.style.Accent_Cyan
            AccentTheme.TEAL -> R.style.Accent_Teal
            AccentTheme.GREEN -> R.style.Accent_Green
            AccentTheme.LIGHT_GREEN -> R.style.Accent_Light_Green
            AccentTheme.LIME -> R.style.Accent_Lime
            AccentTheme.YELLOW -> R.style.Accent_Yellow
            AccentTheme.AMBER -> R.style.Accent_Amber
            AccentTheme.ORANGE -> R.style.Accent_Orange
            AccentTheme.DEEP_ORANGE -> R.style.Accent_Deep_Orange
            AccentTheme.BROWN -> R.style.Accent_Brown
        }
    }

}
