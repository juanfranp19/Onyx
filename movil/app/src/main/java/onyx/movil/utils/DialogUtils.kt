package onyx.movil.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun alertDialog(context: Context, title: String, message: String, positiveBtn: String, funcion: () -> Unit, negativeBtn: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveBtn) { _, _ ->
            funcion()
        }
        .setNegativeButton(negativeBtn, null)
        .show()
}
