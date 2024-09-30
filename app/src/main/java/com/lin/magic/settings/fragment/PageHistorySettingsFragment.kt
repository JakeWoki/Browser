package com.lin.magic.settings.fragment

import com.lin.magic.R
import com.lin.magic.browser.TabsManager
import com.lin.magic.activity.WebBrowserActivity
import com.lin.magic.di.MainScheduler
import com.lin.magic.di.NetworkScheduler
import com.lin.magic.extensions.isDarkTheme
import com.lin.magic.favicon.FaviconModel
import com.lin.magic.settings.preferences.UserPreferences
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebBackForwardList
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import dagger.hilt.android.AndroidEntryPoint
import slions.pref.BasicPreference
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Page history screen
 * TODO: Could add an option to clear the page history?
 * Could also display the number of items in history in the category title
 */
@AndroidEntryPoint
class PageHistorySettingsFragment : AbstractSettingsFragment() {

    @Inject internal lateinit var userPreferences: UserPreferences
    @Inject internal lateinit var tabsManager: TabsManager
    @Inject internal lateinit var faviconModel: FaviconModel

    @Inject @NetworkScheduler
    internal lateinit var networkScheduler: Scheduler
    @Inject @MainScheduler
    internal lateinit var mainScheduler: Scheduler

    override fun providePreferencesXmlResource() = R.xml.preference_page_history

    lateinit var category: PreferenceCategory
    lateinit var history: WebBackForwardList
    private var currentIndex = 0

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState,rootKey)

        category = findPreference<PreferenceCategory>(resources.getString(R.string.pref_key_page_history))?.apply { isOrderingAsAdded = true }!!
        history = tabsManager.currentTab?.webView?.copyBackForwardList()!!
        currentIndex = history.currentIndex
        populateHistory()
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    private fun populateHistory() {

        category.removeAll()

        // Populate current page history
        for ( i in history.size-1 downTo 0) {

            history.getItemAtIndex(i).let {item ->
                // Create history item preference
                val pref = BasicPreference(requireContext())
                //pref.swapTitleSummary = true
                pref.isSingleLineTitle = false
                pref.key = "item$i"
                pref.title = item.title
                if (history.currentIndex==i) {
                    pref.title = "✔ " + pref.title
                }
                pref.summary = item.url
                pref.icon = item.favicon?.scale(com.lin.magic.utils.Utils.dpToPx(24f), com.lin.magic.utils.Utils.dpToPx(24f))?.toDrawable(resources)
                // As favicon is usually null for restored tab we still need to fetch it from our cache
                if (pref.icon==null) {
                    faviconModel.faviconForUrl(item.url,"",context?.isDarkTheme() == true)
                        .subscribeOn(networkScheduler)
                        .observeOn(mainScheduler)
                        .subscribeBy(
                            onSuccess = { bitmap ->
                                pref.icon = bitmap.scale(com.lin.magic.utils.Utils.dpToPx(24f), com.lin.magic.utils.Utils.dpToPx(24f)).toDrawable(resources)
                            }
                        )
                }

                pref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    // Compute to which item we should jump to
                    val steps = i - currentIndex

                    if (steps>0) {
                        // Going forward
                        (activity as WebBrowserActivity).animateTabFlipLeft()
                    } else if (steps<0) {
                        // Going back
                        (activity as WebBrowserActivity).animateTabFlipRight()
                    }

                    tabsManager.currentTab?.webView?.goBackOrForward(steps)
                    // Remove tick from former current item
                    category.findPreference<Preference>("item$currentIndex")?.title = history.getItemAtIndex(currentIndex).title
                    // Update current item
                    currentIndex = i
                    // Add tick to new current item
                    pref.title = "✔ " + pref.title
                    // TODO: Optionally exit our bottom sheet?
                    true
                }

                category.addPreference(pref)
            }
        }
    }



    /**
     * See [AbstractSettingsFragment.titleResourceId]
     */
    override fun titleResourceId(): Int {
        return R.string.settings_title_page_history
    }
}
