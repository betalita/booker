package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 轻之文库
 */
data class LiNovelResponse(val document: Document) {
    fun getBookList() = document.select("div.rank-book-list > a").map { LiNovelItem(it).toBook() }
}

data class LiNovelItem(val element: Element) {
    fun toBook() = Book(SOURCE.LiNovel.name,
            element.selectFirst(".book-name").text(),
            element.selectFirst(".book-extra").text().split("丨").first().trim(),
            element.selectFirst(".book-intro").text().trim(),
            element.selectFirst("img").attr("src"),
            element.selectFirst("a").absUrl("href"),
            element.selectFirst(".book-tag").text().trim()
    )
}