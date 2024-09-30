package com.lin.magic.html.download

import com.lin.magic.R
import com.lin.magic.constant.FILE
import com.lin.magic.database.downloads.DownloadEntry
import com.lin.magic.database.downloads.DownloadsRepository
import com.lin.magic.html.HtmlPageFactory
import com.lin.magic.html.ListPageReader
import com.lin.magic.settings.preferences.UserPreferences
import android.app.Application
import dagger.Reusable
import com.lin.magic.html.jsoup.andBuild
import com.lin.magic.html.jsoup.body
import com.lin.magic.html.jsoup.clone
import com.lin.magic.html.jsoup.id
import com.lin.magic.html.jsoup.parse
import com.lin.magic.html.jsoup.removeElement
import com.lin.magic.html.jsoup.tag
import com.lin.magic.html.jsoup.title
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

/**
 * The factory for the downloads page.
 */
@Reusable
class DownloadPageFactory @Inject constructor(
    private val application: Application,
    private val userPreferences: UserPreferences,
    private val manager: DownloadsRepository,
    private val listPageReader: ListPageReader
) : HtmlPageFactory {

    override fun buildPage(): Single<String> = manager
        .getAllDownloads()
        .map { list ->
            parse(listPageReader.provideHtml()) andBuild {
                title { application.getString(R.string.action_downloads) }
                body {
                    val repeatableElement = id("repeated").removeElement()
                    id("content") {
                        list.forEach {
                            appendChild(repeatableElement.clone {
                                tag("a") { attr("href", createFileUrl(it.title)) }
                                id("title") { text(createFileTitle(it)) }
                                id("url") { text(it.url) }
                            })
                        }
                    }
                }
            }
        }
        .map { content -> Pair(createDownloadsPageFile(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use { it.write(content) }
        }
        .map { (page, _) -> "$FILE$page" }


    private fun createDownloadsPageFile(): File = File(application.filesDir, FILENAME)

    private fun createFileUrl(fileName: String): String = "$FILE${userPreferences.downloadDirectory}/$fileName"

    private fun createFileTitle(downloadItem: DownloadEntry): String {
        val contentSize = if (downloadItem.contentSize.isNotBlank()) {
            "[${downloadItem.contentSize}]"
        } else {
            ""
        }

        return "${downloadItem.title} $contentSize"
    }

    companion object {

        const val FILENAME = "downloads.html"

    }

}
