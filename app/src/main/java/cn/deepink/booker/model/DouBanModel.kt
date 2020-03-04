package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE

/**
 * 豆瓣阅读
 */
data class DouBanItem(val id: String, val title: String, val author: Any, val abstract: String?, val cover: String?, val category: String) {
    fun toBook() = Book(SOURCE.DouBan.name, title, author.toString(), abstract.orEmpty().trim(), cover.orEmpty(), "https://read.douban.com/j/column_v2/$id")
}

data class DouBanBook(val category: String, val isFinished: Boolean, val recVoteCount: Int, val readCount: Int, val total: Int, val wordCount: Int, val lastPublishedChapter: DouBanChapter)

data class DouBanChapter(val title: String, val onSaleTimestamp: Long)