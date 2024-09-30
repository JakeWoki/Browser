package com.lin.magic.adblock

import android.net.Uri
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Unit tests for [NoOpAdBlocker].
 */
class NoOpAdBlockerTest {

    @Test
    fun `isAd no-ops`() {
        val noOpAdBlocker = NoOpAdBlocker()
        val request = com.lin.magic.adblock.TestWebResourceRequest(
            Uri.parse("https://ads.google.com"),
            false,
            mapOf()
        )

        assertThat(noOpAdBlocker.shouldBlock(request, "https://google.com")).isNull()
    }

}
