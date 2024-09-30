/*
 * Copyright © 2020-2021 Jamal Rothfuchs
 * Copyright © 2020-2021 Stéphane Lenclud
 * Copyright © 2015 Anthony Restaino
 */

package com.lin.magic.search.engine

import com.lin.magic.R

/**
 * The Ecosia search engine.
 */
class EcosiaSearch : BaseSearchEngine(
    "file:///android_asset/ecosia.webp",
    "https://www.ecosia.org/search?q=",
    R.string.search_engine_ecosia
)
