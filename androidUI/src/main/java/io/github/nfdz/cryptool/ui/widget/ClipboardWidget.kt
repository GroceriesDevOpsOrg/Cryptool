package io.github.nfdz.cryptool.ui.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import io.github.nfdz.cryptool.ui.R

object ClipboardWidget {

    private const val label = "Cryptool"
    private fun getClipboardManager(context: Context): ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    fun set(context: Context, text: String, showToast:(String) -> Unit) {

        val clipboard = getClipboardManager(context)
        if (clipboard != null && text.isNotEmpty()) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
            showToast(
                context.getString(R.string.cb_copy_success_snackbar),
            )
        } else {
            showToast(
                context.getString(R.string.cb_copy_empty_snackbar),
            )
        }
    }
}