package com.lin.magic.browser.cleanup

import com.lin.magic.Capabilities
import com.lin.magic.activity.MainActivity
import com.lin.magic.activity.WebBrowserActivity
import com.lin.magic.isSupported
import android.webkit.WebView
import javax.inject.Inject

/**
 * Exit cleanup that determines which sort of cleanup to do at runtime. It determines which cleanup
 * to perform based on the API version and whether we are in incognito mode or normal mode.
 */
class DelegatingExitCleanup @Inject constructor(
    private val basicIncognitoExitCleanup: BasicIncognitoExitCleanup,
    private val enhancedIncognitoExitCleanup: EnhancedIncognitoExitCleanup,
    private val normalExitCleanup: NormalExitCleanup
) : ExitCleanup {
    override fun cleanUp(webView: WebView?, context: WebBrowserActivity) {
        when {
            context is MainActivity -> normalExitCleanup.cleanUp(webView, context)
            Capabilities.FULL_INCOGNITO.isSupported -> enhancedIncognitoExitCleanup.cleanUp(webView, context)
            else -> basicIncognitoExitCleanup.cleanUp(webView, context)
        }
    }
}
