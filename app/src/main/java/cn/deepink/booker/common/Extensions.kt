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

fun String.toTime(): Long {
    val current = System.currentTimeMillis()
    val minutes = when {
        contains("分钟前") -> Regex("[0-9]+").find(this)?.value?.toIntOrNull() ?: 0
        contains("小时前") -> (Regex("[0-9]+").find(this)?.value?.toIntOrNull() ?: 0) * 60
        contains("天前") -> (Regex("[0-9]+").find(this)?.value?.toIntOrNull() ?: 0) * 60 * 24
        matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}".toRegex()) -> return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE).parse(this)?.time ?: 0
        else -> 0
    }
    return if (minutes == 0) 0L else current - minutes * 60 * 1000
}

fun String.toNumber() = when {
    contains("万") -> (Regex("[0-9.]+").find(this)?.value?.toFloatOrNull() ?: 0F) * 10000
    contains("千") -> (Regex("[0-9.]+").find(this)?.value?.toFloatOrNull() ?: 0F) * 1000
    else -> Regex("[0-9.]+").find(this)?.value?.toFloatOrNull() ?: 0F
}.toInt()

fun Long.format(): String {
    val minutes = (System.currentTimeMillis() - this) / 1000 / 60
    return when {
        minutes < 60 -> "${minutes}分钟前"
        minutes < 1440 -> "${minutes / 60}小时前"
        minutes < 2880 -> SimpleDateFormat("昨日HH:mm", Locale.CHINESE).format(this)
        else -> SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(this)
    }
}