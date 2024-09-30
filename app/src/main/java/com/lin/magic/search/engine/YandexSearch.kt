/*
 * Copyright © 2020-2021 Jamal Rothfuchs
 * Copyright © 2020-2021 Stéphane Lenclud
 * Copyright © 2015 Anthony Restaino
 */

package com.lin.magic.search.engine

import com.lin.magic.R

/**
 * The Yandex search engine.
 */
class YandexSearch : BaseSearchEngine(
    "file:///android_asset/yandex.webp",
    "https://yandex.ru/yandsearch?lr=21411&text=",
    R.string.search_engine_yandex
)
