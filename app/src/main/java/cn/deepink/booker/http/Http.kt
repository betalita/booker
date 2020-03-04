package cn.deepink.booker.http

import cn.deepink.booker.BuildConfig
import cn.deepink.booker.R
import cn.deepink.booker.model.*
import com.blankj.ALog
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
            SOURCE.HanWuJiNian -> htmlService.hanWuJiNian(bookName).getBookList().apply { ALog.w(this) }
        }?.filter { it.name.contains(bookName) } ?: emptyList()
    } catch (e: Exception) {
        ALog.w(e)
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
enum class SOURCE(val icon: Int, val statistics: Int) {
    QiDian(R.drawable.ic_source_qidian, R.string.book_statistics_qidian),
    JinJiang(R.drawable.ic_source_jjwxc, R.string.book_statistics_jijiang),
    EBTang(R.drawable.ic_source_ebtang, R.string.book_statistics_etbang),
    MoTie(R.drawable.ic_source_motie, R.string.book_statistics_motie),
    HanWuJiNian(R.drawable.ic_source_hanwujinian, R.string.book_statistics_hanwujinian)
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
}

/**
 * Html
 */
class HtmlService {

    fun ebTang(bookName: String): EBTangResponse {
        return EBTangResponse(Jsoup.connect("http://m.ebtang.com/m/book/search?searchName=${bookName}").get())
    }

    fun hanWuJiNian(bookName: String): HanWuJiNianResponse {
        return HanWuJiNianResponse(Jsoup.connect("https://wap.hanwujinian.com/modules/article/search.php?searchkey=${URLEncoder.encode(bookName, "GBK")}&searchtype=all").get().apply {
            ALog.w(this.outerHtml())
        })
    }

}