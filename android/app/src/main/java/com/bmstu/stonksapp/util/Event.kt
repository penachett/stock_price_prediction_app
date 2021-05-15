package com.bmstu.stonksapp.util

open class Event<out T>(private val content: T) {

    var isWatched = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (isWatched) {
            null
        } else {
            isWatched = true
            content
        }
    }

    fun peekContent(): T = content
}