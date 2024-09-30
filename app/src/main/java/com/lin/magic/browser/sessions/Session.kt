//package com.lin.magic.browser.sessions
// We kept this package otherwise it fails to load persisted bundles
// See: https://stackoverflow.com/questions/77292533/load-bundle-from-parcelable-class-after-refactoring
// Could not find an easy solution for it, custom ClassLoader did not work
package acr.browser.lightning.browser.sessions

import android.os.Parcel
import android.os.Parcelable

/**
 * You can easily regenerate that parcelable implementation.
 * See: https://stackoverflow.com/a/49426012/3969362
 * We could also use @Parcelize: https://stackoverflow.com/a/69027267/3969362
 *
 * TODO: Don't use Parcelable as it saves the class name in the Bundle and you can't refactor.
 * Instead do it like we did with [com.lin.magic.browser.TabModel].
 */
data class Session (
    var name: String = "",
    var tabCount: Int = -1,
    var isCurrent: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this() {
        val n = parcel.readString();
        if (n == null) {
            name = ""
        }
        else {
            name = n
        }
        tabCount = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(tabCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Session> {
        override fun createFromParcel(parcel: Parcel): Session {
            return Session(parcel)
        }

        override fun newArray(size: Int): Array<Session?> {
            return arrayOfNulls(size)
        }
    }
}