package com.lin.magic.settings.preferences.delegates

import com.lin.magic.app
import android.content.SharedPreferences
import androidx.annotation.StringRes
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * An [Enum] delegate that is backed by [SharedPreferences].
 *
 * This was persisted using an integer by AR.
 * Then changed by SL to persist the enum name as string instead.
 * Thus making it compatible with [ListPreference] and now [EnumListPreference].
 */
class EnumPreference<T>(
    name: String,
    private val defaultValue: T,
    private val clazz: Class<T>,
    preferences: SharedPreferences
) : ReadWriteProperty<Any, T> where T : Enum<T> {

    //private var backingInt: Int by preferences.intPreference(name, defaultValue.value)
    private var backingValue: String by preferences.stringPreference(name, defaultValue.toString())

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return clazz.enumConstants!!.firstOrNull { it.toString() == backingValue } ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        backingValue = value.toString()
    }
}

/**
 * Creates a [T] enum from [SharedPreferences] with the provide arguments.
 */
inline fun <reified T> SharedPreferences.enumPreference(
    name: String,
    defaultValue: T
): ReadWriteProperty<Any, T> where T : Enum<T> = EnumPreference(
    name,
    defaultValue,
    T::class.java,
    this
)

/**
 * Creates a [T] enum from [SharedPreferences] with the provide arguments.
 */
inline fun <reified T> SharedPreferences.enumPreference(
    @StringRes name: Int,
    defaultValue: T
): ReadWriteProperty<Any, T> where T : Enum<T> = EnumPreference(
    app.resources.getString(name),
    defaultValue,
    T::class.java,
    this
)
