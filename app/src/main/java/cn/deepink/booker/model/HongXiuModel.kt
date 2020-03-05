package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE

/**
 * 搜索
 */
data class HongXiuResponse(val data: HongXiuData) {
    fun getBookList() = data.bookInfo.records.map { it.toBook() }
}

data class HongXiuData(val bookInfo: HongXiuBookInfo, val vs: List<QidianBooklet>, val chapterTotalCnt: Int)

data class HongXiuBookInfo(val records: List<HongXiuRecord>)

data class HongXiuRecord(val bid: Long, val bName: String, val bAuth: String, val desc: String, val imgUrl: String, val cat: String) {
    fun toBook() = Book(SOURCE.HongXiu.name, bName, bAuth, desc, "https:$imgUrl", "https://m.hongxiu.com/book/$bid", cat)
}