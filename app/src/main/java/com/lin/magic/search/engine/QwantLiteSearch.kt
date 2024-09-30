/*
 * Copyright © 2020-2021 Jamal Rothfuchs
 * Copyright © 2020-2021 Stéphane Lenclud
 * Copyright © 2015 Anthony Restaino
 */

package com.lin.magic.search.engine

import com.lin.magic.R

/**
 * The Qwant Lite search engine.
 */
class QwantLiteSearch : BaseSearchEngine(
    "file:///android_asset/qwant.webp",
    "https://lite.qwant.com/?q=",
    R.string.search_engine_qwant_lite
)
