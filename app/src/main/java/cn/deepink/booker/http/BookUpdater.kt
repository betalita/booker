package cn.deepink.booker.http

import cn.deepink.booker.common.Room
import cn.deepink.booker.common.toNumber
import cn.deepink.booker.common.toTime
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
            SOURCE.CiWeiMao -> updateFromCiWeiMao()
            SOURCE.HanWuJiNian -> updateFromHanWuJiNian()
            SOURCE.DouBan -> updateFromDouBan()
            SOURCE.FaLoo -> updateFromFaLoo()
            SOURCE.LiNovel -> updateFromLiNovel()
            SOURCE.HongXiu -> updateFromHongXiu()
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
     * 刺猬猫
     */
    private fun updateFromCiWeiMao() {
        val document = Jsoup.connect(book.link).get()
        book.state = if (document.selectFirst("p.update-state").text().indexOf("连载") == 1) 0 else 1
        book.wordsTotal = document.selectFirst("p.book-grade > b:nth-child(3)").text()
        book.lastChapterName = document.selectFirst("h3.tit > a").text()
        book.lastUpdateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).parse(document.selectFirst("h3.tit > span").text().replace("更新时间：", ""))?.time ?: 0
        book.monthlyTicket = document.selectFirst("li.month > h3").text().toInt()
        book.recommendedTicket = document.selectFirst("li.recommend > h3").text().toInt()
        book.chapterTotal = document.select("ul.book-chapter-list > li").size
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

    /**
     * 豆瓣阅读
     */
    private fun updateFromDouBan() {
        val detail = Http.jsonService.douBanDetail(book.link).execute().body() ?: return
        book.category = detail.category
        book.state = if (detail.isFinished) 0 else 1
        book.wordsTotal = detail.wordCount.toString()
        book.lastChapterName = detail.lastPublishedChapter.title
        book.lastUpdateTime = detail.lastPublishedChapter.onSaleTimestamp * 1000
        book.monthlyTicket = detail.readCount
        book.recommendedTicket = detail.recVoteCount
        book.chapterTotal = detail.total
    }

    /**
     * 飞卢小说网
     */
    private fun updateFromFaLoo() {
        val document = Jsoup.connect(book.link).get()
        book.category = document.selectFirst("div.book_info > div.book_r_box > dl > dd:nth-child(3) > span:nth-child(1) > a").text()
        book.state = if (document.selectFirst("div.book_info > div.book_r_box > dl > dd:nth-child(3) > span:nth-child(2)").text().contains("连载")) 1 else 0
        book.wordsTotal = document.selectFirst("div.book_info > div.book_r_box > dl > dd:nth-child(4) > span:nth-child(2)").text()
        book.lastChapterName = document.selectFirst("p > .chap").text()
        book.lastUpdateTime = SimpleDateFormat("yy-MM-dd", Locale.CHINESE).parse(document.selectFirst("p > .time").text())?.time ?: 0
        book.monthlyTicket = document.selectFirst("#fist_1 > a:nth-child(3) > span").text().toIntOrNull() ?: 0
        book.recommendedTicket = document.selectFirst("#fist_1 > a:nth-child(2) > span").text().toIntOrNull() ?: 0
        book.chapterTotal = Regex("[0-9]+").find(document.selectFirst("#chpaterDirBox > h2").text())?.value?.toIntOrNull() ?: 0
    }

    /**
     * 轻之文库
     */
    private fun updateFromLiNovel() {
        val document = Jsoup.connect(book.link).get()
        book.state = if (document.selectFirst("div.book-data > span:nth-child(7)").text().contains("连载")) 1 else 0
        book.wordsTotal = document.selectFirst("div.book-data > span:nth-child(1)").text()
        book.lastChapterName = document.selectFirst("div.chapter-item.new > a").text()
        book.lastUpdateTime = document.selectFirst("div.chapter-item.new > small").text().trim().toTime()
        book.monthlyTicket = document.selectFirst("div.book-data > span:nth-child(5)").text().toIntOrNull() ?: 0
        book.recommendedTicket = document.selectFirst("div.book-data > span:nth-child(3)").text().toNumber()
        book.chapterTotal = Regex("[0-9]+").find(document.selectFirst("div.tab-bar > a:nth-child(2) > span > small").text().replace(Regex(".+卷"), ""))?.value?.toIntOrNull() ?: 0
    }

    /**
     * 红袖添香
     */
    private fun updateFromHongXiu() {
        val document = Jsoup.connect(book.link).get().body()
        //字数和状态
        val wordsAndState = document.selectFirst("#bookDetailWrapper > div > div.book-layout.book-detail > div > p:nth-child(4)").text().split("|")
        book.wordsTotal = wordsAndState.first()
        book.state = if (wordsAndState.last().contains("连载")) 1 else 0
        //月票
        book.monthlyTicket = document.selectFirst(".month-ticket-cnt").text().toInt()
        //推荐票
        book.recommendedTicket = document.selectFirst(".recomm-ticket-cnt").text().toInt()
        val updateTimeAndChapter = document.selectFirst(".book-meta-r").text().split("·")
        book.lastUpdateTime = updateTimeAndChapter.first().trim().toTime()
        book.lastChapterName = updateTimeAndChapter.last().trim().removePrefix("连载至")
        book.chapterTotal = Jsoup.connect("${book.link}/catalog").get().selectFirst("h4 > output").text().toIntOrNull() ?: 0
    }
}