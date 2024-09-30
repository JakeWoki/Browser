package com.lin.magic.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

/**
 * We rolled out our own ImageView to mitigate those "trying to use a recycled bitmap" exceptions
 * See:
 * - https://github.com/Slion/Magic/issues/376
 * - https://stackoverflow.com/a/68086694/3969362
 */
class ImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
    }

    constructor(context: Context): super(context) {
    }

    override fun onDraw(canvas: Canvas) {
        try {
            super.onDraw(canvas)
        } catch (e: Exception) {
            //Something went wrong, possibly that recycled bitmap issue
            // See: https://github.com/Slion/Magic/issues/376
            e.printStackTrace();
        }
    }
}