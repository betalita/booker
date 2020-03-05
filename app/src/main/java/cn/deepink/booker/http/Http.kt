package cn.deepink.booker.http

import cn.deepink.booker.BuildConfig
import cn.deepink.booker.R
import cn.deepink.booker.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object Http {

    private val retrofit = Retrofit.Builder().baseUrl("https://m.qidian.com").client(buildHttpClient()).addConverterFactory(GsonConverterFactory.create()).build()

    private val htmlService: HtmlService = HtmlService()
    val jsonService: JsonService = retrofit.create(JsonService::class.java)

    fun search(source: SOURCE, bookName: String): List<Book> = try {
        when (source) {
            SOURCE.QiDian -> jsonService.qidian(bookName).execute().body()?.getBookList()
            SOURCE.JinJiang -> jsonService.jinjiang(bookName).execute().body()?.getBookList()
            SOURCE.EBTang -> htmlService.ebTang(bookName).getBookList()
            SOURCE.MoTie -> jsonService.motie(bookName).execute().body()?.getBookList()
            SOURCE.CiWeiMao -> htmlService.ciweimao(bookName).getBookList()
            SOURCE.HanWuJiNian -> htmlService.hanWuJiNian(bookName).getBookList()
            SOURCE.DouBan -> jsonService.douBan(bookName).execute().body()?.filter { it.abstract?.isNotBlank() == true }?.map { it.toBook() }
            SOURCE.FaLoo -> htmlService.faLoo(bookName).getBookList()
        }?.filter { it.name.contains(bookName) } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    private fun buildHttpClient() = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE })
            .build()

}

/**
 * 来源声明
 */
enum class SOURCE(val title: String, val url: String, val icon: Int, val statistics: Int) {
    QiDian("起点中文网", "https://www.qidian.com", R.drawable.ic_source_qidian, R.string.book_statistics_qidian),
    JinJiang("晋江文学城", "http://www.jjwxc.net", R.drawable.ic_source_jjwxc, R.string.book_statistics_jijiang),
    MoTie("磨铁中文网", "http://www.motie.com", R.drawable.ic_source_motie, R.string.book_statistics_motie),
    CiWeiMao("刺猬猫", "https://www.ciweimao.com", R.drawable.ic_source_ciweimao, R.string.book_statistics_ciweimao),
    FaLoo("飞卢小说网", "https://www.faloo.com", R.drawable.ic_source_faloo, R.string.book_statistics_faloo),
    DouBan("豆瓣阅读", "https://read.douban.com", R.drawable.ic_source_douban, R.string.book_statistics_douban),
    EBTang("雁北堂", "http://www.ebtang.com", R.drawable.ic_source_ebtang, R.string.book_statistics_etbang),
    HanWuJiNian("寒武纪年", "https://www.hanwujinian.com", R.drawable.ic_source_hanwujinian, R.string.book_statistics_hanwujinian)
}

/**
 * Json
 */
interface JsonService {

    @GET("majax/search/list")
    fun qidian(@Query("kw") bookName: String): Call<QiDianSearchResponse>

    @GET("majax/book/category")
    fun qidianCatalog(@Query("bookId") bookId: String): Call<QiDianSearchResponse>

    @GET("http://app.jjwxc.org/androidapi/search?type=1&page=1&token=null&searchType=1&sortMode=DESC&versionCode=126")
    fun jinjiang(@Query("keyword") bookName: String): Call<JinJiangResponse>

    @GET
    fun jinjiangDetail(@Url url: String): Call<JinJiangBook>

    @GET("https://app2.motie.com/search?pageNo=1&pageSize=10")
    fun motie(@Query("word") bookName: String): Call<MoTieResponse>

    @GET
    fun motieDetail(@Url url: String): Call<MoTieResponse>

    @GET
    fun hanWuJiNianCatalog(@Url url: String): Call<HanWuJiNianData>

    @GET("https://read.douban.com/j/search?start=0&limit=10")
    fun douBan(@Query("query") bookName: String): Call<List<DouBanItem>>

    @GET
    fun douBanDetail(@Url url: String): Call<DouBanBook>
}

/**
 * Html
 */
class HtmlService {

    fun ebTang(bookName: String): EBTangResponse {
        return EBTangResponse(Jsoup.connect("http://m.ebtang.com/m/book/search?searchName=${bookName}").get())
    }

    fun ciweimao(bookName: String): CiWeiMaoResponse {
        return CiWeiMaoResponse(Jsoup.connect("https://www.ciweimao.com/get-search-book-list/0-0-0-0-0-0/%E5%85%A8%E9%83%A8/${bookName}/1").get())
    }

    fun hanWuJiNian(bookName: String): HanWuJiNianResponse {
        return HanWuJiNianResponse(Jsoup.connect("https://wap.hanwujinian.com/modules/article/search.php?searchkey=${URLEncoder.encode(bookName, "GBK")}&searchtype=all").get())
    }

    fun faLoo(bookName: String): FaLooResponse {
        return FaLooResponse(Jsoup.connect("https://wap.faloo.com/category/0/1.html?k=${URLEncoder.encode(bookName, "GBK")}").get())
    }

}