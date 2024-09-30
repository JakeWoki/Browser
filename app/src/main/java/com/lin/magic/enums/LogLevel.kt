package com.lin.magic.enums

import com.lin.magic.settings.preferences.IntEnum
import android.util.Log

/**
 * Notably used to define what to do when there is a third-party associated with a web site.
 *
 * NOTE: Class name is referenced as strings in our resources.
 */
enum class LogLevel(override val value: Int) :
    IntEnum {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT)
}


