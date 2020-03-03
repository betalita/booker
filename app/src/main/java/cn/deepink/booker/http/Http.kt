package cn.deepink.booker.http

import cn.deepink.booker.BuildConfig
import cn.deepink.booker.R
import cn.deepink.booker.model.EBTangResponse
import cn.deepink.booker.model.JinJiangBook
import cn.deepink.booker.model.JinJiangResponse
import cn.deepink.booker.model.QiDianSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

object Http {

    private val retrofit = Retrofit.Builder().baseUrl("https://m.qidian.com").client(buildHttpClient()).addConverterFactory(GsonConverterFactory.create()).build()

    val jsonService: JsonService = retrofit.create(JsonService::class.java)
    val htmlService: HtmlService = HtmlService()

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
enum class SOURCE(val code: Int, val icon: Int, val statistics: Int) {
    QiDian(0x01, R.drawable.ic_source_qidian, R.string.book_statistics_qidian),
    JinJiang(0x02, R.drawable.ic_source_jjwxc, R.string.book_statistics_jijiang),
    EBTang(0x03, R.drawable.ic_source_ebtang, R.string.book_statistics_etbang)
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

}

/**
 * Html
 */
class HtmlService {

    fun ebTang(bookName: String): EBTangResponse {
        return EBTangResponse(Jsoup.connect("http://m.ebtang.com/m/book/search?searchName=${bookName}").get())
    }

}