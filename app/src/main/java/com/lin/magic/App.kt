package com.lin.magic

import com.lin.magic.activity.IncognitoActivity
import com.lin.magic.database.bookmark.BookmarkRepository
import com.lin.magic.di.DatabaseScheduler
import com.lin.magic.settings.preferences.DeveloperPreferences
import com.lin.magic.settings.preferences.LandscapePreferences
import com.lin.magic.settings.preferences.PortraitPreferences
import com.lin.magic.settings.preferences.UserPreferences
import com.lin.magic.utils.installMultiDex
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import com.lin.magic.settings.Config
import com.lin.magic.settings.preferences.ConfigurationPreferences
import io.reactivex.Scheduler
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess


@SuppressLint("StaticFieldLeak")
lateinit var app: App

@HiltAndroidApp
class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener,
    Application.ActivityLifecycleCallbacks {

    @Inject internal lateinit var developerPreferences: DeveloperPreferences
    @Inject internal lateinit var userPreferences: UserPreferences
    @Inject internal lateinit var portraitPreferences: PortraitPreferences
    @Inject internal lateinit var landscapePreferences: LandscapePreferences
    @Inject internal lateinit var bookmarkModel: BookmarkRepository
    @Inject @DatabaseScheduler
    internal lateinit var databaseScheduler: Scheduler

    // Provide global access to current configuration preferences
    internal var configPreferences: ConfigurationPreferences? = null

    //@Inject internal lateinit var buildInfo: BuildInfo

    // Used to be able to tell when our application was just started
    var justStarted: Boolean = true
    //Ugly way to pass our domain around for settings
    var domain: String = ""
    //Ugly way to pass our config around for settings
    var config = Config("")

    /**
     * Our app can runs in a different process when using the incognito activity.
     * This tells us which process it is.
     * However this is initialized after the activity creation when running on versions before Android 9.
     */
    var incognito = false
        private set(value) {
            if (value) {
                Timber.d("Incognito app process")
            }
            field = value
        }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // We only need to install that multi DEX library when not doing minify code optimization, typically in debug, and on devices below API level 21.
        // In fact from API level 21 and above Android Runtime (ART) is used rather than deprecated Dalvik.
        // Since ART has multi DEX support built-in we don't need to install that DEX library from API level 21 and above.
        // See: https://github.com/Slion/Magic/issues/116
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT < 21) {
            installMultiDex(context = base)
        }
    }


    /**
     * Setup Timber log engine according to user preferences
     */
    private fun plantTimberLogs() {

        // Update Timber
        if (userPreferences.logs) {
            Timber.uprootAll()
            Timber.plant(TimberLevelTree(userPreferences.logLevel.value))
        } else {
            Timber.uprootAll()
        }

        // Test our logs
        Timber.v("Log verbose")
        Timber.d("Log debug")
        Timber.i("Log info")
        Timber.w("Log warn")
        Timber.e("Log error")
        // We disabled that as we don't want our process to terminate
        // Though it did not terminate the app in debug configuration on Huawei P30 Pro - Android 10
        //Timber.wtf("Log assert")
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.v("onActivityCreated")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (activity is IncognitoActivity) {
                // Needed as the process check we use below does not work before Android 9
                incognito = true
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.v("onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.v("onActivityResumed")
        resumedActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.v("onActivityPaused")
        resumedActivity = null
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.v("onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.v("onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.v("onActivityDestroyed")
        com.lin.magic.utils.MemoryLeakUtils.clearNextServedView(activity, this@App)
    }


    /**
     *
     */
    override fun onCreate() {
        app = this
        registerActivityLifecycleCallbacks(this)
        // SL: Use this to debug when launched from another app for instance
        //Debug.waitForDebugger()
        super.onCreate()
        // No need to unregister I suppose cause this is for the life time of the application anyway
        userPreferences.preferences.registerOnSharedPreferenceChangeListener(this)

        plantTimberLogs()
        Timber.v("onCreate")

        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG) {
            /*
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
             */
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (getProcessName() == "$packageName:incognito") {
                incognito = true
                WebView.setDataDirectorySuffix("incognito")
            }
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            if (userPreferences.crashLogs) {
                com.lin.magic.utils.FileUtils.writeCrashToStorage(ex)
            }

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex)
            } else {
                exitProcess(2)
            }
        }

	// TODO: Remove that once we are done with ReactiveX
        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            if (userPreferences.crashLogs && throwable != null) {
                com.lin.magic.utils.FileUtils.writeCrashToStorage(throwable)
                throw throwable
            }
        }

        // Apply locale
        val requestLocale = com.lin.magic.locale.LocaleUtils.requestedLocale(userPreferences.locale)
        com.lin.magic.locale.LocaleUtils.updateLocale(this, requestLocale)

        // Import default bookmarks if none present
        // Now doing this synchronously as on fast devices it could result in not showing the bookmarks on first start
        if (bookmarkModel.count()==0L) {
            Timber.d("Create default bookmarks")
            val assetsBookmarks = com.lin.magic.database.bookmark.BookmarkExporter.importBookmarksFromAssets(this@App)
            bookmarkModel.addBookmarkList(assetsBookmarks)
        }

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

    }


    companion object {

        // Used to track current activity
        // Apparently we take care of not leaking it above
        @SuppressLint("StaticFieldLeak")
        var resumedActivity: Activity? = null
            private set

        /**
         * Used to get current activity context in order to access current theme.
         */
        fun currentContext() : Context {
            return resumedActivity
                ?: app
        }

        /**
         * Was needed to patch issue with Homepage displaying system language when user selected another language
         */
        fun setLocale() {
            val requestLocale = com.lin.magic.locale.LocaleUtils.requestedLocale(app.userPreferences.locale)
            com.lin.magic.locale.LocaleUtils.updateLocale(app, requestLocale)
        }

    }

    /**
     *
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == getString(R.string.pref_key_log_level) || key == getString(R.string.pref_key_logs)) {
            // Update Timber according to changed preferences
            plantTimberLogs()
        }
    }

}
