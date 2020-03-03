package cn.deepink.booker.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.deepink.booker.R
import cn.deepink.booker.common.Room
import cn.deepink.booker.http.BookUpdater
import cn.deepink.booker.model.Book
import cn.deepink.framework.provider.ContextProvider.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.CHINESE)

    val bookLiveData = Room.book().getAll()

    /**
     * 添加分组标题
     */
    fun addBookGroup(books: MutableList<Book>): MutableList<Book> {
        val today = dateFormat.format(Date()).toInt()
        var indexOf = books.indexOfFirst { dateFormat.format(it.lastUpdateTime).toInt() == today }
        if (indexOf > -1) {
            books.add(indexOf, Book("", context.getString(R.string.today)))
        }
        indexOf = books.indexOfFirst { it.lastUpdateTime > 0 && today - dateFormat.format(it.lastUpdateTime).toInt() == 1 }
        if (indexOf > -1) {
            books.add(indexOf, Book("", context.getString(R.string.yestoday)))
        }
        indexOf = books.indexOfFirst { it.lastUpdateTime > 0 && today - dateFormat.format(it.lastUpdateTime).toInt() > 1 }
        if (indexOf > -1) {
            books.add(indexOf, Book("", context.getString(R.string.old_day)))
        }
        return books
    }

    /**
     * 检查更新
     */
    fun checkUpdate(): LiveData<Boolean> = liveData {
        withContext(Dispatchers.IO) {
            Room.book().getNeedCheckUpdate().forEach { book -> BookUpdater(book).checkUpdate() }
        }
        emit(false)
    }
}