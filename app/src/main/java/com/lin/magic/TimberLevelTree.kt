package com.lin.magic

import timber.log.Timber

/**
 * Timber tree which logs messages from the specified priority.
 */
class TimberLevelTree(private val iPriority: Int) : Timber.DebugTree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= iPriority
    }

}