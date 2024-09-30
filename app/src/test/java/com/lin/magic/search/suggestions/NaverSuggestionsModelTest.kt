package com.lin.magic.search.suggestions

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.*

/**
 * Unit tests for [NaverSuggestionsModel].
 */
class NaverSuggestionsModelTest {

    private val httpClient = Single.just(OkHttpClient.Builder().build())
    private val requestFactory = object :
        RequestFactory {
        override fun createSuggestionsRequest(httpUrl: HttpUrl, encoding: String) =
            com.lin.magic.unimplemented()
    }
    private val mockConfiguration = mock<Configuration> {
        on { locales } doReturn LocaleList(Locale.US)
    }.apply {
        locale = Locale.US
    }

    private val mockResources = mock<Resources> {
        on { configuration } doReturn mockConfiguration
    }
    private val application = mock<Application> {
        on { getString(any()) } doReturn "test"
        on { resources } doReturn mockResources
    }
/*
    @Test
    fun `verify query url`() {
        val model = NaverSuggestionsModel(httpClient, requestFactory, application, NoOpLogger())

        (0..100).forEach {
            val result = "https://ac.search.naver.com/nx/ac?q=$it&q_enc=UTF-8&st=100&frm=nv&r_format=json&r_enc=UTF-8&r_unicode=0&t_koreng=1&ans=2&run=2&rev=4&con=1"

            assertThat(model.createQueryUrl(it.toString(), "null")).isEqualTo(result.toHttpUrlOrNull())
        }
    }
    */
}
