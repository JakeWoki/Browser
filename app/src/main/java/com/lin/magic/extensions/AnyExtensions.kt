package com.lin.magic.extensions

/**
 * Identity Hash Code
 */
val Any.ihc : Int
    get() = System.identityHashCode(this)

/**
 * Identity Hash Code as hexadecimal encoded string.
 * Notably useful to use in logs for objects with multiple instances.
 */
val Any.ihs : String
    get() = "%08X".format(ihc)

