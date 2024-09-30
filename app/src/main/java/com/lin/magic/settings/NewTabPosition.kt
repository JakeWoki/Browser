package com.lin.magic.settings

import com.lin.magic.settings.preferences.IntEnum

/**
 * Define where new tab should be spawned
 *
 * TODO: Move this to enum package
 */
enum class NewTabPosition(override val value: Int) :
    IntEnum {
    BEFORE_CURRENT_TAB(0),
    AFTER_CURRENT_TAB(1),
    START_OF_TAB_LIST(2),
    END_OF_TAB_LIST(3)
}
