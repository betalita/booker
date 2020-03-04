package cn.deepink.booker.model

import cn.deepink.booker.common.removeHtml
import cn.deepink.booker.http.SOURCE
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 雁北堂
 */
data class EBTangResponse(val document: Document) {
    fun getBookList() = document.select("#bookList > li").map { EBTangItem(it).toBook() }
}

data class EBTangItem(val element: Element) {
    fun toBook() = Book(SOURCE.EBTang.name,
            name = element.attr("d-name").removeHtml(),
            author = element.attr("d-nick").removeHtml(),
            summary = element.attr("d-info").removeHtml(),
            cover = element.attr("d-cover"),
            category = element.attr("d-sort"),
            link = "http://m.ebtang.com/m/book/${element.attr("d-id")}"
    )
}