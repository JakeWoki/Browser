package com.lin.magic.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import android.view.ViewConfiguration

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.lang.reflect.Method
import kotlin.math.abs

/**
 * This is working around the following issue: https://github.com/Slion/Magic/issues/60
 * See: https://stackoverflow.com/a/23989911/3969362
 *
 * It improves to odds of not running into the issue while not fixing it.
 * If you still force pull-to-refresh while scrolling you can trigger the bug preventing you to open new page until you restart the app.
 */
class PullRefreshLayout(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var mTouchDownX = 0f
    //private var mTouchDownY = 0f
    private var mIntercept = true

    private val LOG_TAG = PullRefreshLayout::class.java.simpleName

    private val iMethodEnsureTarget: Method = SwipeRefreshLayout::class.java.getDeclaredMethod("ensureTarget")
    private val iFieldTarget = SwipeRefreshLayout::class.java.getDeclaredField("mTarget")

    // I vaguely remember that Kotlin init blocks are called in order of declaration.
    // Since this comes after iMethodEnsureTarget was set we should be fine.
    init {
        iMethodEnsureTarget.isAccessible = true
        iFieldTarget.isAccessible = true
    }

    /**
     * Calls private method [SwipeRefreshLayout.ensureTarget].
     */
    private fun callEnsureTarget() {
        iMethodEnsureTarget.invoke(this)
    }

    /**
     * Get value of private field [SwipeRefreshLayout.mTarget].
     */
    private fun getTarget() : View? {
        return iFieldTarget.get(this) as View?
    }

    /**
     *
     */
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // Prevent crash when SwipeRefreshLayout does not have a target
        // See: https://github.com/Slion/Magic/issues/285
        callEnsureTarget()
        if (getTarget()==null) {
            //Log.d(LOG_TAG,"================FIXED-IT================::onTouchEvent")
            return false;
        }

        return super.onTouchEvent(ev)
    }

    /**
     *
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        // Prevent crash when SwipeRefreshLayout does not have a target
        // See: https://github.com/Slion/Magic/issues/285
        callEnsureTarget()
        if (getTarget()==null) {
            //Log.d(LOG_TAG,"================FIXED-IT================")
            return false;
        }

        when (event.action) {
            // Mark X position of our touchdown
            MotionEvent.ACTION_DOWN -> {
                mTouchDownX = event.x
                //mTouchDownY = event.y
                mIntercept = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIntercept) {
                    return false;
                }
                // Check if we think user is scrolling vertically
                val eventX = event.x
                val xDiff = abs(eventX - mTouchDownX)
                if (xDiff > mTouchSlop) {
                    // User is scrolling horizontally do not intercept inputs
                    // Thus preventing pull-to-refresh to trigger while we scroll horizontally
                    mIntercept = false
                }
            }
        }

        if (mIntercept) {
            return super.onInterceptTouchEvent(event)
        }

        return false;
    }

}