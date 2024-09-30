package com.lin.magic

import com.lin.magic.settings.preferences.IntEnum

/**
 * The available app themes.
 */
enum class AppTheme(override val value: Int) :
    IntEnum {
    LIGHT(0),
    DARK(1),
    BLACK(2),
    DEFAULT(3)
}
