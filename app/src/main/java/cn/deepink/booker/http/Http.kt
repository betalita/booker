package cn.deepink.booker.http

import cn.deepink.booker.R
import cn.deepink.booker.model.*
import com.blankj.BuildConfig
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
enum class SOURCE(val icon: Int, val statistics: Int) {
    QiDian(R.drawable.ic_source_qidian, R.string.book_statistics_qidian),
    JinJiang(R.drawable.ic_source_jjwxc, R.string.book_statistics_jijiang),
    EBTang(R.drawable.ic_source_ebtang, R.string.book_statistics_etbang),
    CiWeiMao(R.drawable.ic_source_ciweimao, R.string.book_statistics_ciweimao),
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

    fun ciweimao(bookName: String): CiWeiMaoResponse {
        return CiWeiMaoResponse(Jsoup.connect("https://www.ciweimao.com/get-search-book-list/0-0-0-0-0-0/%E5%85%A8%E9%83%A8/${bookName}/1").get())
    }

}