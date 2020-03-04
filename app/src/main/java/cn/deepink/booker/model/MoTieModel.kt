package cn.deepink.booker.model

import cn.deepink.booker.http.SOURCE

/**
 * 磨铁中文网
 */
data class MoTieResponse(val data: MoTieData) {
    fun getBookList() = data.bookList.map { it.toBook() }
}

data class MoTieData(val bookList: List<MoTieBook>, val finished: Boolean, val lastChapterList: List<MoTieChapter>, val sortName: String, val visitCount: Int, val supportCount: Int, val words: Long, val chapterCount: Int)

data class MoTieBook(val id: Int, val name: String, val authorName: String, val introduce: String, val icon: String) {
    fun toBook() = Book(SOURCE.MoTie.name, name, authorName, introduce, icon, "https://app2.motie.com/books/${id}/detail")
}

data class MoTieChapter(val name: String, val showTime: Long)