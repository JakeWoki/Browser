package com.lin.magic

import com.lin.magic.settings.preferences.IntEnum

/**
 * Define sponsorships level.
 * Declared in root package so that it can be used from BuildConfig.
 */
enum class Sponsorship(override val value: Int) :
    IntEnum {
    TIN(0),
    BRONZE(1),
    SILVER(2),
    GOLD(3),
    PLATINUM(4),
    DIAMOND(5)
}
