package com.lin.magic.dialog

import com.lin.magic.R
import com.lin.magic.extensions.inflater
import com.lin.magic.list.RecyclerViewStringAdapter
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter

/**
 * Pager adapter instantiate pager items.
 *
 */
class TabsPagerAdapter(
    private val context: Context,
    private val dialog: AlertDialog,
    private val tabs: List<DialogTab>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Inflate our view from our layout definition
        val view: View = context.inflater.inflate(R.layout.dialog_tab_list, container, false)
        // Populate our list with our items
        val recyclerView = view.findViewById<RecyclerView>(R.id.dialog_list)
        val itemList = tabs[position].iItems.filter(DialogItem::show)
        val adapter = RecyclerViewStringAdapter(itemList, getTitle = { context.getString(this.title) }, getText = {this.text})
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = adapter
            setHasFixedSize(true)
        }

        adapter.onItemClickListener = { item ->
            item.onClick()
            dialog.dismiss()
        }

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View);
    }

    /**
     * See: https://stackoverflow.com/questions/30995446/what-is-the-role-of-isviewfromobject-view-view-object-object-in-fragmentst
     */
    override fun isViewFromObject(aView: View, aObject: Any): Boolean {
        return aView === aObject
    }

    override fun getCount(): Int {
        return tabs.count()
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (tabs[position].title == 0) {
            return ""
        }
        return context.getString(tabs[position].title)
    }

    /**
     * Convert zero-based numbering of tabs into readable numbering of tabs starting at 1.
     *
     * @param position - Zero-based tab position
     * @return Readable tab position
     */
    private fun getReadableTabPosition(position: Int): Int {
        return position + 1
    }
}