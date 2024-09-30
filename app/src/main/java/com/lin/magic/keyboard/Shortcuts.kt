package com.lin.magic.keyboard

import com.lin.magic.R
import android.content.Context
import android.os.Build
import android.view.KeyEvent
import android.view.KeyboardShortcutInfo
import androidx.annotation.RequiresApi
import java.util.ArrayList

/**
 * Define our keyboard shortcuts.
 * Allows us to publish our keyboard shortcuts enabling user quick references using Meta+/.?
 * TODO: Somehow make BrowserActivity use this to trigger actions then make the shortcuts customizable.
 */
@RequiresApi(Build.VERSION_CODES.N)
class Shortcuts(aContext: Context) {

    val iList: ArrayList<KeyboardShortcutInfo> = ArrayList()

    init {

        // NOTE: For some reason KeyboardShortcutInfo with Icon can't be called, no icons then.
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_cycle_tabs_forwards), KeyEvent.KEYCODE_TAB, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_cycle_tabs_backwards), KeyEvent.KEYCODE_TAB, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_tab_history_forward), KeyEvent.KEYCODE_FORWARD, 0))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_reload), KeyEvent.KEYCODE_F5, 0))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_reload), KeyEvent.KEYCODE_R, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_focus_address_bar), KeyEvent.KEYCODE_F6, 0))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_focus_address_bar), KeyEvent.KEYCODE_L, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_toggle_status_bar), KeyEvent.KEYCODE_F10, 0))
        iList.add(KeyboardShortcutInfo(aContext.getText(R.string.action_toggle_toolbar), KeyEvent.KEYCODE_F11, 0))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_text_size_decrement), KeyEvent.KEYCODE_MINUS, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_text_size_small_decrement), KeyEvent.KEYCODE_MINUS, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_text_size_increment), KeyEvent.KEYCODE_EQUALS, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_text_size_small_increment), KeyEvent.KEYCODE_EQUALS, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_add_bookmark), KeyEvent.KEYCODE_B, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_open_bookmark_list), KeyEvent.KEYCODE_B, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_open_tab_list), KeyEvent.KEYCODE_P, KeyEvent.META_CTRL_ON ))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_open_tab_list), KeyEvent.KEYCODE_T, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_new_tab), KeyEvent.KEYCODE_T, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_duplicate_tab), KeyEvent.KEYCODE_D, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_open_session_list), KeyEvent.KEYCODE_S, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.exit), KeyEvent.KEYCODE_Q, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_find), KeyEvent.KEYCODE_F, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_find_next), KeyEvent.KEYCODE_F3, 0))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_find_previous), KeyEvent.KEYCODE_F3, KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_find_selection), KeyEvent.KEYCODE_F3, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.close_tab), KeyEvent.KEYCODE_W, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.close_tab), KeyEvent.KEYCODE_F4, KeyEvent.META_CTRL_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_switch_to_session), KeyEvent.KEYCODE_1, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_switch_to_last_session), KeyEvent.KEYCODE_0, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_switch_to_tab), KeyEvent.KEYCODE_1, KeyEvent.META_CTRL_ON ))
        iList.add(KeyboardShortcutInfo(aContext.getString(R.string.action_switch_to_last_tab), KeyEvent.KEYCODE_0, KeyEvent.META_CTRL_ON ))
    }

}