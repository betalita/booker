package cn.deepink.booker.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.deepink.booker.http.Http
import cn.deepink.booker.http.SOURCE
import cn.deepink.booker.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min

class SearchViewModel : ViewModel() {

    //搜索状态
    var searchStateLiveData = MutableLiveData<Boolean>()
    //搜索词
    var searchKey = ""

    /**
     * 快捷搜索 用于实时搜索提示
     */
    fun quickSearch(bookName: String): LiveData<List<Book>> = liveData(Dispatchers.IO) {
        searchStateLiveData.postValue(true)
        searchKey = bookName
        if (searchKey.isNotBlank()) {
            val bookInfoList = Http.search(SOURCE.QiDian, bookName)
            emit(bookInfoList.subList(0, min(bookInfoList.size, 8)).reversed())
        } else {
            emit(emptyList())
        }
    }

    /**
     * 搜索
     */
    fun search(bookName: String): LiveData<List<Book>> = liveData {
        searchStateLiveData.postValue(false)
        searchKey = bookName
        val bookList = CopyOnWriteArrayList<Book>()
        withContext(Dispatchers.Default) {
            SOURCE.values().forEach { source ->
                launch(Dispatchers.IO) {
                    bookList.addAll(Http.search(source, bookName))
                    emit(bookList.toList())
                }
                Unit
            }
        }
    }
}