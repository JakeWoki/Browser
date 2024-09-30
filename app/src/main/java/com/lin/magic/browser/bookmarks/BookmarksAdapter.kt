package com.lin.magic.browser.bookmarks

import com.lin.magic.R
import com.lin.magic.browser.WebBrowser
import com.lin.magic.database.Bookmark
import com.lin.magic.database.bookmark.BookmarkRepository
import com.lin.magic.extensions.drawable
import com.lin.magic.extensions.isDarkTheme
import com.lin.magic.extensions.setImageForTheme
import com.lin.magic.favicon.FaviconModel
import com.lin.magic.utils.ItemDragDropSwipeAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class BookmarksAdapter(
    val context: Context,
    val webBrowser: WebBrowser,
    private val bookmarksRepository: BookmarkRepository,
    private val faviconModel: FaviconModel,
    private val networkScheduler: Scheduler,
    private val mainScheduler: Scheduler,
    private val databaseScheduler: Scheduler,
    private val iShowBookmarkMenu: (Bookmark) -> Boolean,
    private val iOpenBookmark: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkViewHolder>(),
    ItemDragDropSwipeAdapter {

    private var bookmarks: List<BookmarksViewModel> = listOf()
    private val faviconFetchSubscriptions = ConcurrentHashMap<String, Disposable>()
    private val folderIcon = context.drawable(R.drawable.ic_folder)
    private val webpageIcon = context.drawable(R.drawable.ic_webpage)


    fun itemAt(position: Int): BookmarksViewModel = bookmarks[position]

    fun deleteItem(item: BookmarksViewModel) {
        val newList = bookmarks - item
        updateItems(newList)
    }

    /**
     *
     */
    fun updateItems(newList: List<BookmarksViewModel>) {
        val oldList = bookmarks
        bookmarks = newList

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = bookmarks.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldList[oldItemPosition].bookmark.url == bookmarks[newItemPosition].bookmark.url

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldList[oldItemPosition] == bookmarks[newItemPosition]
        })

        diffResult.dispatchUpdatesTo(this)
    }

    fun cleanupSubscriptions() {
        for (subscription in faviconFetchSubscriptions.values) {
            subscription.dispose()
        }
        faviconFetchSubscriptions.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.bookmark_list_item, parent, false)

        return BookmarkViewHolder(itemView, this, iShowBookmarkMenu, iOpenBookmark)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.itemView.jumpDrawablesToCurrentState()

        val viewModel = bookmarks[position]
        holder.txtTitle.text = viewModel.bookmark.title

        val url = viewModel.bookmark.url
        holder.favicon.tag = url

        viewModel.icon?.let {
            holder.favicon.setImageBitmap(it)
            return
        }

        val imageDrawable = when (viewModel.bookmark) {
            is Bookmark.Folder -> folderIcon
            is Bookmark.Entry -> webpageIcon.also {
                faviconFetchSubscriptions[url]?.dispose()
                faviconFetchSubscriptions[url] = faviconModel
                        .faviconForUrl(url, viewModel.bookmark.title, context.isDarkTheme())
                        .subscribeOn(networkScheduler)
                        .observeOn(mainScheduler)
                        .subscribeBy(
                                onSuccess = { bitmap ->
                                    viewModel.icon = bitmap
                                    if (holder.favicon.tag == url) {
                                        holder.favicon.setImageForTheme(bitmap, context.isDarkTheme())
                                    }
                                }
                        )
            }
        }

        holder.favicon.setImageDrawable(imageDrawable)
    }

    override fun getItemCount() = bookmarks.size

    /**
     * Implements [ItemDragDropSwipeAdapter.onItemMove]
     * An item was was moved through drag & drop
     */
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    {
        val source = bookmarks[fromPosition].bookmark
        val destination = bookmarks[toPosition].bookmark
        // We can only swap bookmark entries not folders
        if (!(source is Bookmark.Entry && destination is Bookmark.Entry)) {
            // Folder are shown last in our list for now so we just can't order them
            return false
        }

        // Swap local list positions
        Collections.swap(bookmarks, fromPosition, toPosition)

        // Due to our database definition we need edit position of each bookmarks in current folder
        // Go through our list and edit position as needed
        var position = 0;
        bookmarks.toList().forEach { b ->
            if (b.bookmark is Bookmark.Entry) {
                if (b.bookmark.position != position || position==fromPosition || position==toPosition) {
                    val editedItem = Bookmark.Entry(
                            title = b.bookmark.title,
                            url = b.bookmark.url,
                            folder = b.bookmark.folder,
                            position = position
                    )

                    position++

                    bookmarksRepository.editBookmark(b.bookmark, editedItem)
                    .subscribeOn(databaseScheduler)
                    .observeOn(mainScheduler).let {
                        if (position!=bookmarks.count()){
                            it.subscribe()
                        } else {
                            // Broadcast update only for our last operation
                            // Though I have no idea if our operations are FIFO
                            it.subscribe(webBrowser::handleBookmarksChange)
                        }
                    }
                }
            }
        }

        // Tell base class an item was moved
        notifyItemMoved(fromPosition, toPosition)

        return true;
    }

    /**
     * Implements [ItemDragDropSwipeAdapter.onItemDismiss]
     */
    override fun onItemDismiss(position: Int)
    {

    }
}
