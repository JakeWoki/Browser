/*
 * Copyright © 2020-2021 Jamal Rothfuchs
 * Copyright © 2020-2021 Stéphane Lenclud
 * Copyright © 2015 Anthony Restaino
 */

package com.lin.magic.search.engine

import com.lin.magic.R

/**
 * The DuckDuckGo search engine.
 */
class DuckSearch : BaseSearchEngine(
    "file:///android_asset/duckduckgo.webp",
    "https://duckduckgo.com/?t=magic&q=",
    R.string.search_engine_duckduckgo
)
