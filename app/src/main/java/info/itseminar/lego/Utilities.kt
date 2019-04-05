package info.itseminar.lego

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String, longLasting: Boolean = false) {
    Toast
        .makeText(
                this,
                message,
                if (longLasting) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                )
        .show()
    }