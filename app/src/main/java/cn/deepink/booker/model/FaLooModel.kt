package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 飞卢小说网
 */
data class FaLooResponse(val document: Document) {
    fun getBookList() = document.select("div.book_vessel").map { FaLooItem(it).toBook() }
}

data class FaLooItem(val element: Element) {
    fun toBook() = Book(SOURCE.FaLoo.name, element.selectFirst(".show_title2").text(), element.selectFirst(".show_author > a").text(), element.selectFirst(".show_desc2").text(), element.selectFirst("img").attr("src"), element.selectFirst("a").absUrl("href"))
}