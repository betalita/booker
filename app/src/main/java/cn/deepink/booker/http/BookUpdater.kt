package cn.deepink.booker.http

import cn.deepink.booker.common.Room
import cn.deepink.booker.model.Book
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class BookUpdater(private val book: Book) {


    /**
     * 检查更新
     */
    fun checkUpdate(isUpdateDatabase: Boolean = true) {
        when (book.sourceType) {
            SOURCE.QiDian -> updateFromQidian()
            SOURCE.JinJiang -> updateFromJinJiang()
            SOURCE.EBTang -> updateFromEBTang()
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
        Http.jsonService.qidianCatalog(Regex("[0-9]+").find(book.link)?.value.orEmpty()).execute().body()?.data?.run {
            book.chapterTotal = chapterTotalCnt
            book.lastChapterName = vs.last().cs.last().cN
            book.lastUpdateTime = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINESE).parse(vs.last().cs.last().uT)?.time ?: 0
        }
    }

    /**
     * 晋江文学城
     */
    private fun updateFromJinJiang() {
        val detail = Http.jsonService.jinjiangDetail(book.link).execute().body() ?: return
        book.state = if (detail.novelStep == 1) 1 else 0
        book.wordsTotal = detail.novelSize
        book.chapterTotal = detail.novelChapterCount
        book.lastChapterName = detail.renewChapterName
        book.lastUpdateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).parse(detail.renewDate)?.time ?: 0
        book.monthlyTicket = detail.novelbefavoritedcount
        book.recommendedTicket = detail.nutrition_novel
    }

    /**
     * 雁北堂
     */
    private fun updateFromEBTang() {
        val document = Jsoup.connect(book.link).get()
        val detail = document.selectFirst("#bookDetail")
        book.state = if (detail.attr("d-finish") == "0") 1 else 0
        book.wordsTotal = detail.attr("d-words")
        book.lastChapterName = detail.attr("d-lasttitle")
        book.lastUpdateTime = detail.attr("d-lasttime").toLong()
        book.monthlyTicket = detail.attr("d-golden").toInt()
        book.recommendedTicket = detail.attr("d-hot").toInt()
        book.chapterTotal = Jsoup.connect("${book.link}/directory").get().select("b.chapter").size
    }
}