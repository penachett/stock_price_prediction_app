package com.bmstu.stonksapp.source

import android.util.Log
import com.bmstu.stonksapp.StonksApp
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle.ofApplicationForeground
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class TinkoffSocketService(private val scope: CoroutineScope, private val token: String) {

    private lateinit var service: SocketService
    private lateinit var eventsChannel: ReceiveChannel<WebSocket.Event>
    private lateinit var dataChannel: ReceiveChannel<Any>

    init {
        initService()
    }

    private fun initService() {
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        builder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header(AUTH_HEADER_NAME, "$AUTH_HEADER_PREFIX $token").build())
        })

        service = Scarlet.Builder()
            .webSocketFactory(builder.build().newWebSocketFactory(SOCKET_URL))
//            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .lifecycle(ofApplicationForeground(StonksApp.getInstance()))
            .backoffStrategy(LinearBackoffStrategy(RECONNECT_INTERVAL_MS))
            .build().create(SocketService::class.java)
        eventsChannel = service.observeEvents()
        dataChannel = service.observeDataMessages()
        listenData()
        listenEvents()
    }

    private fun listenEvents() {
        scope.launch {
            for (event in eventsChannel) {
                Log.i(TAG, event.toString())
            }
        }
    }

    private fun listenData() {
        scope.launch {
            for (data in dataChannel) {
                Log.i(TAG, data.toString())
            }
        }
    }

    fun sendString(string: String) {
        service.send(string)
    }

    companion object {
        private const val TAG = "Tinkoff Source"
        private const val SOCKET_URL = "wss://api-invest.tinkoff.ru/openapi/md/v1/md-openapi/ws"
        const val AUTH_HEADER_NAME = "Authorization"
        const val AUTH_HEADER_PREFIX = "Bearer"
//        private const val SOCKET_URL = "wss://echo.websocket.org"
//        private const val SOCKET_URL = "wss://dev.whitecabs.ru/base/v2/connect"
        private const val RECONNECT_INTERVAL_MS = 1000L
        private const val TIMEOUT_SECONDS = 10L
        const val CONNECTION_FAILED = "failed"
        const val CONNECTION_OPENED = "opened"
    }
}