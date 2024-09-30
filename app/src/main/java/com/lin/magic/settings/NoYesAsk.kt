package com.lin.magic.settings

import com.lin.magic.settings.preferences.IntEnum

/**
 * Notably used to define what to do when there is a third-party associated with a web site.
 *
 * NOTE: Class name is referenced as strings in our resources.
 *
 * TODO: Move this to enum package?
 */
enum class NoYesAsk(override val value: Int) :
    IntEnum {
    NO(0),
    YES(1),
    ASK(2)
}


