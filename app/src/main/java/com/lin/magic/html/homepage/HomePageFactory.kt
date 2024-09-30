package com.lin.magic.html.homepage

import com.lin.magic.App
import com.lin.magic.R
import com.lin.magic.constant.FILE
import com.lin.magic.constant.UTF8
import com.lin.magic.html.HtmlPageFactory
import com.lin.magic.search.SearchEngineProvider
import com.lin.magic.settings.preferences.UserPreferences
import com.lin.magic.utils.ThemeUtils
import com.lin.magic.utils.htmlColor
import android.app.Application
import dagger.Reusable
import com.lin.magic.html.jsoup.andBuild
import com.lin.magic.html.jsoup.body
import com.lin.magic.html.jsoup.charset
import com.lin.magic.html.jsoup.id
import com.lin.magic.html.jsoup.parse
import com.lin.magic.html.jsoup.tag
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

/**
 * A factory for the home page.
 */
@Reusable
class HomePageFactory @Inject constructor(
    private val application: Application,
    private val searchEngineProvider: SearchEngineProvider,
    private val homePageReader: HomePageReader,
    private var userPreferences: UserPreferences
) : HtmlPageFactory {

    override fun buildPage(): Single<String> = Single
        .just(searchEngineProvider.provideSearchEngine())
        .map { (iconUrl, queryUrl, _) ->
	    App.setLocale() // Make sure locale is set as user specified
            parse(homePageReader.provideHtml()
                .replace("\${TITLE}", application.getString(R.string.home))
                .replace("\${backgroundColor}", htmlColor(ThemeUtils.getSurfaceColor(App.currentContext())))
                .replace("\${searchBarColor}", htmlColor(ThemeUtils.getSearchBarColor(ThemeUtils.getSurfaceColor(App.currentContext()))))
                .replace("\${searchBarTextColor}", htmlColor(ThemeUtils.getColor(App.currentContext(),R.attr.colorOnSurface)))
                .replace("\${borderColor}", htmlColor(ThemeUtils.getColor(App.currentContext(),R.attr.colorOnSecondary)))
                .replace("\${accent}", htmlColor(ThemeUtils.getColor(App.currentContext(),R.attr.colorSecondary)))
                .replace("\${search}", application.getString(R.string.search_homepage))
            ) andBuild {
                charset { UTF8 }
                body {
                    when (userPreferences.searchChoice) {
                        0 -> id("image_url") { attr("src", userPreferences.imageUrlString) }
                        else -> id("image_url") { attr("src", iconUrl) }
                    }
                    tag("script") {
                        html(
                            html()
                                .replace("\${BASE_URL}", queryUrl)
                                .replace("&", "\\u0026")
                        )
                    }
                }
            }
        }
        .map { content -> Pair(createHomePage(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use {
                it.write(content)
            }
        }
        .map { (page, _) -> "$FILE$page" }

    /**
     * Create the home page file.
     */
    fun createHomePage() = File(application.filesDir, FILENAME)

    companion object {

        const val FILENAME = "homepage.html"

    }

}
