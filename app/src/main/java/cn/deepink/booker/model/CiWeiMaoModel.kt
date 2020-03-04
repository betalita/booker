package cn.deepink.booker.model

import cn.deepink.booker.common.removeHtml
import cn.deepink.booker.http.SOURCE
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 刺猬猫
 */

data class CiWeiMaoResponse(val document: Document) {
    fun getBookList() = document.select("div.rank-book-list > ul > li").map { CiWeiMaoItem(it).toBook() }
}

data class CiWeiMaoItem(val element: Element) {
    fun toBook() = Book(SOURCE.CiWeiMao.name,
            name = element.selectFirst("p.tit > a").text().removeHtml(),
            author = element.selectFirst("div.cnt > p:nth-child(2) > a").text().removeHtml(),
            summary = element.selectFirst(".desc").text().removeHtml(),
            cover = element.selectFirst("img").attr("src"),
            link = element.selectFirst("p.tit > a").attr("href")
    )
}