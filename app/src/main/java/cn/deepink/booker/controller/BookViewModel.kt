package cn.deepink.booker.controller

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.deepink.booker.R
import cn.deepink.booker.common.Room
import cn.deepink.booker.common.TAG_HOME
import cn.deepink.booker.common.TAG_SEARCH
import cn.deepink.booker.http.BookUpdater
import cn.deepink.booker.model.Book
import kotlinx.coroutines.Dispatchers

class BookViewModel : ViewModel() {

    lateinit var book: Book

    fun attach(intent: Intent): Book? {
        val tag = if (intent.hasExtra(TAG_HOME)) TAG_HOME else TAG_SEARCH
        book = intent.getParcelableExtra(tag) ?: return null
        return book
    }

    /**
     * 更新
     */
    fun update(): LiveData<Book> = liveData(Dispatchers.IO) {
        if (!Room.book().isExist(book.link)) {
            BookUpdater(book).checkUpdate(false)
        }
        emit(book)
    }

    /**
     * 是否已订阅
     */
    fun hasSubscribed(): LiveData<Int> = liveData(Dispatchers.IO) {
        emit(if (Room.book().isExist(book.link)) R.string.unsubscribe else R.string.subscribe)
    }

    /**
     * 订阅
     */
    fun subscribe(): LiveData<Int> = liveData(Dispatchers.IO) {
        if (Room.book().isExist(book.link)) {
            Room.book().deleteByLink(book.link)
            emit(R.string.subscribe)
        } else {
            Room.book().insert(book)
            emit(R.string.unsubscribe)
        }
    }


}