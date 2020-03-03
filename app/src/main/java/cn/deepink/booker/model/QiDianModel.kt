package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE

/**
 * 搜索
 */
data class QiDianSearchResponse(val data: QiDianData) {
    fun getBookList() = data.bookInfo.records.map { it.toBook() }
}

data class QiDianData(val bookInfo: QiDianBookInfo, val vs: List<QidianBooklet>, val chapterTotalCnt: Int)

data class QiDianBookInfo(val records: List<QiDianRecord>)

data class QiDianRecord(val bid: Long, val bName: String, val bAuth: String, val desc: String, val imgUrl: String, val cat: String) {
    fun toBook() = Book(SOURCE.QiDian.name, bName, bAuth, desc, "https:$imgUrl", "https://m.qidian.com/book/$bid", cat)
}

/**
 * 目录
 */
data class QidianBooklet(val cs: List<QidianChapter>)

data class QidianChapter(val cN: String, val uT: String)