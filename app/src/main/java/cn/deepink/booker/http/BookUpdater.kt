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
            SOURCE.MoTie -> updateFromMoTie()
            SOURCE.HanWuJiNian -> updateFromHanWuJiNian()
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

    /**
     * 磨铁
     */
    private fun updateFromMoTie() {
        val detail = Http.jsonService.motieDetail(book.link).execute().body()?.data ?: return
        book.category = detail.sortName
        book.state = if (detail.finished) 0 else 1
        book.wordsTotal = detail.words.toString()
        book.lastChapterName = detail.lastChapterList.firstOrNull()?.name.orEmpty()
        book.lastUpdateTime = detail.lastChapterList.firstOrNull()?.showTime ?: 0
        book.monthlyTicket = detail.visitCount
        book.recommendedTicket = detail.supportCount
        book.chapterTotal = detail.chapterCount
    }

    /**
     * 寒武纪年
     */
    private fun updateFromHanWuJiNian() {
        val document = Jsoup.connect(book.link).get()
        book.state = if (document.selectFirst("h1 > .tag").text().contains("连载")) 1 else 0
        book.wordsTotal = document.selectFirst("div.other").text().split("，").first()
        book.monthlyTicket = Regex("[0-9]+").find(document.selectFirst(".center-operate > a:nth-child(2)").text())?.value?.toIntOrNull() ?: 0
        book.recommendedTicket = Regex("[0-9]+").find(document.selectFirst(".center-operate > a:nth-child(1)").text())?.value?.toIntOrNull() ?: 0
        val catalog = Http.jsonService.hanWuJiNianCatalog("${book.link.replace("modules/article/articleinfo.php?id", "riku/read/bookmenupage.php?aid")}&order=2&offset=0&limit=1000&uid=0").execute().body()?.data ?: return
        book.chapterTotal = catalog.size
        book.lastChapterName = catalog.first().chaptername
        book.lastUpdateTime = catalog.first().lastupdate * 1000
    }
}