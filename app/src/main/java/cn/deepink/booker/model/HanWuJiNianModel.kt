package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 寒武纪年
 */
data class HanWuJiNianResponse(val document: Document) {

    fun getBookList() = document.select(".g-bookInfo").map { HanWuJiNianBook(it).toBook() }

}

data class HanWuJiNianBook(val element: Element) {
    fun toBook() = Book(SOURCE.HanWuJiNian.name, element.selectFirst(".title").text(), element.selectFirst(".author").text(), element.selectFirst(".txt").text(), element.selectFirst(".book > a > img").attr("src"), element.selectFirst(".book > a").attr("href"), element.selectFirst(".tags > span").text())
}

data class HanWuJiNianData(val data: List<HanWuJiNianChapter>)

data class HanWuJiNianChapter(val chaptername: String, val lastupdate: Long)