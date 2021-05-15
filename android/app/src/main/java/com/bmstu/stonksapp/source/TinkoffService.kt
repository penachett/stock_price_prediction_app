package com.bmstu.stonksapp.source

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface TinkoffService {
    @Send
    fun send(message: Any): Boolean

    @Receive
    fun observeEvents(): ReceiveChannel<WebSocket.Event>

    @Receive
    fun observeDataMessages(): ReceiveChannel<Any>
}