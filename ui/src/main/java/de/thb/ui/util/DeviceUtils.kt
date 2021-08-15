package de.thb.ui.util

import android.content.Context
import android.util.TypedValue

fun pxFromDp(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}
