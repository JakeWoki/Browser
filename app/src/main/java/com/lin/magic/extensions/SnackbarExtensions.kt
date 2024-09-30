package com.lin.magic.extensions

import com.lin.magic.R
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar

/**
 * Adds an extra action button to this snackbar.
 * [aLayoutId] must be a layout with a Button as root element.
 * [aLabel] defines new button label string.
 * [aListener] handles our new button click event.
 */
fun Snackbar.addAction(@LayoutRes aLayoutId: Int, @StringRes aLabel: Int, aListener: View.OnClickListener?) : Snackbar {
    addAction(aLayoutId,context.getString(aLabel),aListener)
    return this;
}

/**
 * Adds an extra action button to this snackbar.
 * [aLayoutId] must be a layout with a Button as root element.
 * [aLabel] defines new button label string.
 * [aListener] handles our new button click event.
 */
fun Snackbar.addAction(@LayoutRes aLayoutId: Int, aLabel: String, aListener: View.OnClickListener?) : Snackbar {
    // Add our button
    val button = LayoutInflater.from(view.context).inflate(aLayoutId, null) as Button
    // Using our special knowledge of the snackbar action button id we can hook our extra button next to it
    view.findViewById<Button>(R.id.snackbar_action).let {
        // Copy layout
        button.layoutParams = it.layoutParams
        // Copy colors
        (button as? Button)?.setTextColor(it.textColors)
        (it.parent as? ViewGroup)?.addView(button)
    }
    button.text = aLabel
    /** Ideally we should use [Snackbar.dispatchDismiss] instead of [Snackbar.dismiss] though that should do for now */
    //extraView.setOnClickListener {this.dispatchDismiss(BaseCallback.DISMISS_EVENT_ACTION); aListener?.onClick(it)}
    button.setOnClickListener {this.dismiss(); aListener?.onClick(it)}
    return this;
}

/**
 * Add an icon to this snackbar.
 * See: https://stackoverflow.com/a/31829381/3969362
 */
fun Snackbar.setIcon(drawable: Drawable): Snackbar {
    return this.apply {
        //setAction(" ") {}
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        textView.compoundDrawablePadding = context.resources.getDimensionPixelOffset(com.google.android.material.R.dimen.m3_navigation_item_icon_padding);
    }
}

/**
 *  Add an icon to this snackbar.
 */
fun Snackbar.setIcon(@DrawableRes aIcon: Int): Snackbar {
    return this.apply {
        AppCompatResources.getDrawable(context, aIcon)?.let {
            // Apply proper tint so that it works regardless of the theme
            it.setTint(MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurfaceInverse, Color.BLACK))
            setIcon(it)
        }
    }
}