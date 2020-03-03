package cn.deepink.booker.http

import cn.deepink.booker.BuildConfig
import cn.deepink.booker.model.JinJiangBook
import cn.deepink.booker.model.JinJiangResponse
import cn.deepink.booker.model.QiDianSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

object Http {

    private val retrofit = Retrofit.Builder().baseUrl("https://m.qidian.com").client(buildHttpClient()).addConverterFactory(GsonConverterFactory.create()).build()

    val search: SearchService = retrofit.create(SearchService::class.java)

    private fun buildHttpClient() = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE })
        .build()

}

interface SearchService {

    @GET("majax/search/list")
    fun qidian(@Query("kw") bookName: String): Call<QiDianSearchResponse>

    @GET("majax/book/category")
    fun qidianCatalog(@Query("bookId") bookId: String): Call<QiDianSearchResponse>

    @GET("http://app.jjwxc.org/androidapi/search?type=1&page=1&token=null&searchType=1&sortMode=DESC&versionCode=126")
    fun jinjiang(@Query("keyword") bookName: String): Call<JinJiangResponse>

    @GET
    fun jinjiangDetail(@Url url: String): Call<JinJiangBook>

}