package com.lin.magic.di

import com.lin.magic.browser.cleanup.DelegatingExitCleanup
import com.lin.magic.browser.cleanup.ExitCleanup
import com.lin.magic.database.adblock.UserRulesDatabase
import com.lin.magic.database.adblock.UserRulesRepository
import com.lin.magic.database.bookmark.BookmarkDatabase
import com.lin.magic.database.bookmark.BookmarkRepository
import com.lin.magic.database.downloads.DownloadsDatabase
import com.lin.magic.database.downloads.DownloadsRepository
import com.lin.magic.database.history.HistoryDatabase
import com.lin.magic.database.history.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dependency injection module used to bind implementations to interfaces.
 * SL: Looks like those are still actually needed.
 */
@Module
@InstallIn(SingletonComponent::class)
interface AppBindsModule {

    @Binds
    fun bindsExitCleanup(delegatingExitCleanup: DelegatingExitCleanup): ExitCleanup

    @Binds
    fun bindsBookmarkModel(bookmarkDatabase: BookmarkDatabase): BookmarkRepository

    @Binds
    fun bindsDownloadsModel(downloadsDatabase: DownloadsDatabase): DownloadsRepository

    @Binds
    fun bindsHistoryModel(historyDatabase: HistoryDatabase): HistoryRepository

    @Binds
    fun bindsAbpRulesRepository(apbRulesDatabase: UserRulesDatabase): UserRulesRepository

}
