package com.lin.magic.di

import com.lin.magic.adblock.AbpBlockerManager
import com.lin.magic.adblock.AbpUserRules
import com.lin.magic.adblock.NoOpAdBlocker
import com.lin.magic.browser.TabsManager
import com.lin.magic.database.bookmark.BookmarkRepository
import com.lin.magic.database.downloads.DownloadsRepository
import com.lin.magic.database.history.HistoryRepository
import com.lin.magic.dialog.LightningDialogBuilder
import com.lin.magic.favicon.FaviconModel
import com.lin.magic.html.homepage.HomePageFactory
import com.lin.magic.js.InvertPage
import com.lin.magic.js.SetMetaViewport
import com.lin.magic.js.TextReflow
import com.lin.magic.network.NetworkConnectivityModel
import com.lin.magic.search.SearchEngineProvider
import com.lin.magic.settings.preferences.UserPreferences
import com.lin.magic.view.webrtc.WebRtcPermissionsModel
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.SharedPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Scheduler

/**
 * Provide access to all our injectable classes.
 * Virtual fields can't resolve qualifiers for some reason.
 * Therefore we use functions where qualifiers are needed.
 *
 * Just add your class here if you need it.
 *
 * TODO: See if and how we can use the 'by' syntax to initialize usage of those.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltEntryPoint {

    val bookmarkRepository: BookmarkRepository
    val userPreferences: UserPreferences
    @UserPrefs
    fun userSharedPreferences(): SharedPreferences
    val historyRepository: HistoryRepository
    @DatabaseScheduler
    fun databaseScheduler(): Scheduler
    @NetworkScheduler
    fun networkScheduler(): Scheduler
    @DiskScheduler
    fun diskScheduler(): Scheduler
    @MainScheduler
    fun mainScheduler(): Scheduler
    val searchEngineProvider: SearchEngineProvider
    val proxyUtils: com.lin.magic.utils.ProxyUtils
    val textReflowJs: TextReflow
    val invertPageJs: InvertPage
    val setMetaViewport: SetMetaViewport
    val homePageFactory: HomePageFactory
    val abpBlockerManager: AbpBlockerManager
    val noopBlocker: NoOpAdBlocker
    val dialogBuilder: LightningDialogBuilder
    val networkConnectivityModel: NetworkConnectivityModel
    val faviconModel: FaviconModel
    val webRtcPermissionsModel: WebRtcPermissionsModel
    val abpUserRules: AbpUserRules
    val downloadHandler: com.lin.magic.download.DownloadHandler
    val downloadManager: DownloadManager
    val downloadsRepository: DownloadsRepository
    var tabsManager: TabsManager
    var clipboardManager: ClipboardManager

}


