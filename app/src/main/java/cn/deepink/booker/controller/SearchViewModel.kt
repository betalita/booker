package cn.deepink.booker.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.deepink.booker.common.SOURCE_JINJIANG
import cn.deepink.booker.common.SOURCE_QIDIAN
import cn.deepink.booker.http.Http
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
            val bookInfoList = Http.search.qidian(bookName).execute().body()?.getBookList()?.filter { it.name.contains(bookName) }.orEmpty()
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
            listOf(SOURCE_QIDIAN, SOURCE_JINJIANG).forEach { source ->
                launch(Dispatchers.IO) {
                    when (source) {
                        SOURCE_QIDIAN -> bookList.addAll(searchByQidian(bookName))
                        SOURCE_JINJIANG -> bookList.addAll(searchByJinJiang(bookName))
                    }
                    emit(bookList.toList())
                }
                Unit
            }
        }
    }

    /**
     * 起点中文网
     */
    private fun searchByQidian(bookName: String) = try {
        Http.search.qidian(bookName).execute().body()?.getBookList()?.filter { it.name.contains(bookName) }.orEmpty()
    }catch (e: Exception) {
        emptyList<Book>()
    }

    /**
     * 晋江文学城
     */
    private fun searchByJinJiang(bookName: String) = try {
        Http.search.jinjiang(bookName).execute().body()?.getBookList()?.filter { it.name.contains(bookName) }.orEmpty()
    }catch (e: Exception) {
        emptyList<Book>()
    }

}