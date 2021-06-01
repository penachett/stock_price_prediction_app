package com.bmstu.stonksapp.source

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HttpService {

    companion object {
        private const val STONKS_BASE_URL = "http://84.252.142.175:9990/"
        private const val TINKOFF_BASE_URL = "https://api-invest.tinkoff.ru/openapi/sandbox/"
        private const val TIMEOUT_SECONDS = 12L

        fun getTinkoffApi(token: String): TinkoffHttpApi =
            getRetrofit(tinkoffService = true, token = token).create(TinkoffHttpApi::class.java)

        fun getStonksApi(): StonksAppApi = getRetrofit().create(StonksAppApi::class.java)

        private fun getRetrofit(tinkoffService: Boolean = false, token: String? = null): Retrofit {

            val clientBuilder = OkHttpClient().newBuilder()
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (tinkoffService) {
                if (token != null) {
                    clientBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                        chain.proceed(
                            chain.request().newBuilder()
                                .header(
                                    TinkoffSocketService.AUTH_HEADER_NAME,
                                    "${TinkoffSocketService.AUTH_HEADER_PREFIX} $token"
                                ).build()
                        )
                    })
                }
            } else {
                clientBuilder.cookieJar(SessionCookieJar())
            }

            return Retrofit.Builder()
                .baseUrl(if (tinkoffService) TINKOFF_BASE_URL else STONKS_BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private class SessionCookieJar : CookieJar {
            private var cookies: List<Cookie>? = null
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                if (url.encodedPath.endsWith("login") || url.encodedPath.endsWith("register")) {
                    this.cookies = ArrayList(cookies)
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return if (!url.encodedPath.endsWith("login")
                    && !url.encodedPath.endsWith("register") && cookies != null) {
                    cookies!!
                } else Collections.emptyList()
            }
        }
    }
}