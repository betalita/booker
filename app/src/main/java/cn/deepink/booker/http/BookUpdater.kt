package cn.deepink.booker.http

import cn.deepink.booker.common.Room
import cn.deepink.booker.common.SOURCE_JINJIANG
import cn.deepink.booker.common.SOURCE_QIDIAN
import cn.deepink.booker.model.Book
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class BookUpdater(private val book: Book) {


    /**
     * 检查更新
     */
    fun checkUpdate(isUpdateDatabase: Boolean = true) {
        when (book.source) {
            SOURCE_QIDIAN -> updateFromQidian()
            SOURCE_JINJIANG -> updateFromJinJiang()
        }
        if (isUpdateDatabase) {
            Room.book().update(book)
        }
    }

    /**
     * 起点中文网
     */
    private fun updateFromQidian() {
        val document = Jsoup.connect(book.link).get().body()
        //字数和状态
        val wordsAndState = document.selectFirst(".book-cell > .book-meta:last-child").text().split("|")
        book.wordsTotal = wordsAndState.first()
        book.state = if (wordsAndState.last().contains("连载")) 1 else 0
        //月票
        book.monthlyTicket = document.selectFirst(".month-ticket-cnt").text().toInt()
        //推荐票
        book.recommendedTicket = document.selectFirst(".recomm-ticket-cnt").text().toInt()
        //章节总数
        Http.search.qidianCatalog(Regex("[0-9]+").find(book.link)?.value.orEmpty()).execute().body()?.data?.run {
            book.chapterTotal = chapterTotalCnt
            book.lastChapterName = vs.last().cs.last().cN
            book.lastUpdateTime = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINESE).parse(vs.last().cs.last().uT)?.time ?: 0
        }
    }

    /**
     * 晋江文学城
     */
    private fun updateFromJinJiang() {
        val detail = Http.search.jinjiangDetail(book.link).execute().body() ?: return
        book.state = if (detail.novelStep == 1) 1 else 0
        book.wordsTotal = detail.novelSize
        book.chapterTotal = detail.novelChapterCount
        book.lastChapterName = detail.renewChapterName
        book.lastUpdateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).parse(detail.renewDate)?.time ?: 0
        book.monthlyTicket = detail.novelbefavoritedcount
        book.recommendedTicket = detail.nutrition_novel
    }
}