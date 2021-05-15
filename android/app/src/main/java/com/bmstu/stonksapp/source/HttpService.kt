package com.bmstu.stonksapp.source

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HttpService {

    companion object {
        private const val BASE_URL = ""
        private const val TINKOFF_BASE_URL = "https://api-invest.tinkoff.ru/openapi/sandbox"
        private const val TIMEOUT_SECONDS = 10L

        fun getTinkoffApi(token: String): TinkoffHttpApi = getRetrofit(token = token)
            .create(TinkoffHttpApi::class.java)

        fun getStonksApi(): StonksAppApi = getRetrofit(cookiesEnabled = true).create(StonksAppApi::class.java)

        private fun getRetrofit(cookiesEnabled: Boolean = false, token: String? = null): Retrofit {

            val clientBuilder = OkHttpClient().newBuilder()
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (cookiesEnabled) {
                clientBuilder.cookieJar(SessionCookieJar())
            } else {
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
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
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
                    && !url.encodedPath.endsWith("version")
                    && !url.encodedPath.endsWith("register") && cookies != null) {
                    cookies!!
                } else Collections.emptyList()
            }
        }
    }
}