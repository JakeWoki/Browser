package com.lin.magic.html.bookmark

import com.lin.magic.R
import com.lin.magic.activity.WebBrowserActivity
import com.lin.magic.constant.FILE
import com.lin.magic.database.Bookmark
import com.lin.magic.database.bookmark.BookmarkRepository
import com.lin.magic.di.DatabaseScheduler
import com.lin.magic.di.DiskScheduler
import com.lin.magic.di.configPrefs
import com.lin.magic.extensions.isDarkTheme
import com.lin.magic.extensions.safeUse
import com.lin.magic.favicon.FaviconModel
import com.lin.magic.favicon.toValidUri
import com.lin.magic.html.HtmlPageFactory
import com.lin.magic.settings.preferences.UserPreferences
import com.lin.magic.utils.ThemeUtils
import com.lin.magic.utils.htmlColor
import android.app.Application
import android.graphics.Bitmap
import androidx.core.net.toUri
import dagger.Reusable
import com.lin.magic.App
import com.lin.magic.html.jsoup.andBuild
import com.lin.magic.html.jsoup.body
import com.lin.magic.html.jsoup.clone
import com.lin.magic.html.jsoup.id
import com.lin.magic.html.jsoup.parse
import com.lin.magic.html.jsoup.removeElement
import com.lin.magic.html.jsoup.tag
import com.lin.magic.html.jsoup.title
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import javax.inject.Inject

/**
 * Created by anthonycr on 9/23/18.
 *
 * Generates our bookmarks HTML page.
 * We actually use it as our default home page these days.
 */
@Reusable
class BookmarkPageFactory @Inject constructor(
    private val application: Application,
    private val bookmarkModel: BookmarkRepository,
    private val faviconModel: FaviconModel,
    @DatabaseScheduler private val databaseScheduler: Scheduler,
    @DiskScheduler private val diskScheduler: Scheduler,
    private val bookmarkPageReader: BookmarkPageReader,
    private val userPreferences: UserPreferences,
) : HtmlPageFactory {

    private val title = application.getString(R.string.action_bookmarks)
    private val folderIconFile by lazy { File(application.cacheDir, FOLDER_ICON) }
    private val folderIconFileOnDark by lazy { File(application.cacheDir, FOLDER_ICON_ON_DARK) }
    private val defaultIconFile by lazy { File(application.cacheDir, DEFAULT_ICON) }

    override fun buildPage(): Single<String> = bookmarkModel
        .getAllBookmarksSorted()
        .flattenAsObservable { it }
        .groupBy<Bookmark.Folder, Bookmark>(Bookmark.Entry::folder) { it }
        .flatMapSingle { bookmarksInFolder ->
            val folder = bookmarksInFolder.key
            return@flatMapSingle bookmarksInFolder
                .toList()
                .concatWith(
                    if (folder == Bookmark.Folder.Root) {
                        bookmarkModel.getFoldersSorted().map { it.filterIsInstance<Bookmark.Folder.Entry>() }
                    } else {
                        Single.just(emptyList())
                    }
                )
                .toList()
                .map { bookmarksAndFolders ->
                    Pair(folder, bookmarksAndFolders.flatten().map { it.asViewModel() })
                }
        }
        .map { (folder, viewModels) -> Pair(folder, construct(viewModels)) }
        .subscribeOn(databaseScheduler)
        .observeOn(diskScheduler)
        .doOnNext { (folder, content) ->
            FileWriter(createBookmarkPage(folder), false).use {
                it.write(content)
            }
        }
        .ignoreElements()
        .toSingle {
            cacheIcon(ThemeUtils.createThemedBitmap(application, R.drawable.ic_folder, false), folderIconFile)
            cacheIcon(ThemeUtils.createThemedBitmap(application, R.drawable.ic_folder, true), folderIconFileOnDark)
            cacheIcon(faviconModel.createDefaultBitmapForTitle(null), defaultIconFile)

            "$FILE${createBookmarkPage(null)}"
        }

    private fun cacheIcon(icon: Bitmap, file: File) = FileOutputStream(file).safeUse {
        icon.compress(Bitmap.CompressFormat.PNG, 100, it)
        icon.recycle()
    }

    private fun construct(list: List<BookmarkViewModel>): String {
        val useDarkTheme = (App.currentContext() as? WebBrowserActivity)?.isDarkTheme() == false
        return parse(bookmarkPageReader.provideHtml()
            // Theme our page first
            .replace("\${useDarkTheme}", useDarkTheme.toString()) // Not actually used for now
            .replace("\${colorBackground}", htmlColor(ThemeUtils.getBackgroundColor(App.currentContext())))
            .replace("\${colorOnBackground}", htmlColor(ThemeUtils.getColor(App.currentContext(),R.attr.colorOnSurface)))
            .replace("\${colorControl}", htmlColor(ThemeUtils.getSearchBarColor(ThemeUtils.getSurfaceColor(App.currentContext()))))
            .replace("\${colorBorder}", htmlColor(ThemeUtils.getColor(App.currentContext(),R.attr.colorOutline)))
        ) andBuild {
            title { title }
            body {
                val repeatableElement = id("repeated").removeElement()
                id("content") {
                    list.forEach {
                        val newElement = repeatableElement.clone {
                            tag("a") { attr("href", it.url) }
                            // Make sure we use proper icon for dark themes
                            tag("img") { attr("src", if (useDarkTheme && it.iconUrlOnDark.isNotEmpty()) it.iconUrlOnDark else it.iconUrl) }
                            id("title") { appendText(it.title) }
                        }
                        if (application.configPrefs.toolbarsBottom) {
                            prependChild(newElement)
                        } else {
                            appendChild(newElement)
                        }

                    }
                }
            }
        }
    }

    private fun Bookmark.asViewModel(): BookmarkViewModel = when (this) {
        is Bookmark.Folder -> createViewModelForFolder(this)
        is Bookmark.Entry -> createViewModelForBookmark(this)
    }

    private fun createViewModelForFolder(folder: Bookmark.Folder): BookmarkViewModel {
        val folderPage = createBookmarkPage(folder)
        val url = "$FILE$folderPage"

        return BookmarkViewModel(
            title = folder.title,
            url = url,
            iconUrl = folderIconFile.toString(),
            iconUrlOnDark = folderIconFileOnDark.toString()
        )
    }

    private fun createViewModelForBookmark(entry: Bookmark.Entry): BookmarkViewModel {
        val bookmarkUri = entry.url.toUri().toValidUri()

        // Fetch icon URL for light theme
        val iconUrl = if (bookmarkUri != null) {
            val faviconFile = FaviconModel.getFaviconCacheFile(application, bookmarkUri,false)
            if (!faviconFile.exists()) {
                val defaultFavicon = faviconModel.createDefaultBitmapForTitle(entry.title)
                faviconModel.cacheFaviconForUrl(defaultFavicon, entry.url)
                    .subscribeOn(diskScheduler)
                    .subscribe()
            }

            faviconFile
        } else {
            defaultIconFile
        }

        // Fetch icon URL for dark theme if any
        val iconUrlOnDark = if (bookmarkUri != null) {
            val faviconFile = FaviconModel.getFaviconCacheFile(application, bookmarkUri,true)
            if (!faviconFile.exists()) {
                ""
            }
            else {
                faviconFile.toString()
            }
        }
        else
        {
            ""
        }


        return BookmarkViewModel(
            title = entry.title,
            url = entry.url,
            iconUrl = iconUrl.toString(),
            iconUrlOnDark = iconUrlOnDark
        )
    }

    /**
     * Create the bookmark page file.
     */
    fun createBookmarkPage(folder: Bookmark.Folder?): File {
        val prefix = if (folder?.title?.isNotBlank() == true) {
            "${folder.title}-"
        } else {
            ""
        }
        return File(application.filesDir, prefix + FILENAME)
    }

    companion object {

        const val FILENAME = "bookmarks.html"

        private const val FOLDER_ICON = "folder.png"
        private const val FOLDER_ICON_ON_DARK = "folder-on-dark.png"
        private const val DEFAULT_ICON = "default.png"

    }
}
