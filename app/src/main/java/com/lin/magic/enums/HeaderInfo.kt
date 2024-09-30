package com.lin.magic.enums

/**
 * Define which information should be displayed in a header such as address bar, task bar and task switcher.
 *
 * NOTES:
 * - Class name is referenced as string in our resources.
 * - Enum string values are stored in preferences.
 */
enum class HeaderInfo {
    Url,
    ShortUrl,
    Domain,
    Title,
    Session,
    AppName
}


