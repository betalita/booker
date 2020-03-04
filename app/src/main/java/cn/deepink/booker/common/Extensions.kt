package cn.deepink.booker.common

import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.*

fun TextView.setDrawableStart(@DrawableRes drawableRes: Int) {
    val drawable = if (drawableRes != 0) context.getDrawable(drawableRes) else null
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
}

fun String.removeHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

fun Long.format(): String {
    val minutes = (System.currentTimeMillis() - this) / 1000 / 60
    return when {
        minutes < 60 -> "${minutes}分钟前"
        minutes < 1440 -> "${minutes / 60}小时前"
        minutes < 2880 -> SimpleDateFormat("昨日HH:mm", Locale.CHINESE).format(this)
        else -> SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(this)
    }
}