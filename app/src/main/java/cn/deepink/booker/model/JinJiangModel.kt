package cn.deepink.booker.model

import cn.deepink.booker.common.SOURCE_JINJIANG

data class JinJiangResponse(val items: List<JinJiangItem>) {
    fun getBookList() = items.map { it.toBook() }
}

data class JinJiangItem(val novelid: Long, val novelname: String, val authorname: String, val novelintro: String, val cover: String, val novelClass: String) {

    fun toBook() = Book(SOURCE_JINJIANG, novelname, authorname, novelintro.trim(), cover, "http://app-cdn.jjwxc.net:80/androidapi/novelbasicinfo?novelId=$novelid", novelClass.split("-").first())

}

data class JinJiangBook(val novelStep: Int, val novelSize: String, val novelChapterCount: Int, val renewDate: String, val renewChapterName: String, val novelbefavoritedcount: Int, val nutrition_novel: Int)