package com.bmstu.stonksapp.source

import com.bmstu.stonksapp.StonksApp
import com.tinder.scarlet.Lifecycle.State.Started
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle.ofApplicationForeground
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class TinkoffSource {

    private lateinit var service: TinkoffService
    private lateinit var socketLifecycle: LifecycleRegistry
    private lateinit var eventsChannel: ReceiveChannel<WebSocket.Event>
    private lateinit var dataChannel: ReceiveChannel<Any>

    init {
        initService()
    }

    fun initService() {
        socketLifecycle = LifecycleRegistry(0)
        socketLifecycle.onNext(Started)
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        service = Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(SOCKET_URL))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .lifecycle(ofApplicationForeground(StonksApp.getInstance()))
            .backoffStrategy(LinearBackoffStrategy(RECONNECT_INTERVAL_MS))
            .build().create(TinkoffService::class.java)
        eventsChannel = service.observeEvents()
        dataChannel = service.observeDataMessages()
    }

    companion object {
        private const val SOCKET_URL = "wss://api-invest.tinkoff.ru/openapi/md/v1/md-openapi/ws"
        const val CONNECTION_FAILED = "failed"
        const val CONNECTION_OPENED = "opened"
        private const val RECONNECT_INTERVAL_MS = 1000L
    }
}