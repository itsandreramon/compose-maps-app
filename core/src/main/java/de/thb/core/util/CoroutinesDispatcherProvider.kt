package de.thb.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

interface CoroutinesDispatcherProvider {
    fun main(): CoroutineDispatcher
    fun computation(): CoroutineDispatcher
    fun disk(): CoroutineDispatcher
    fun network(): CoroutineDispatcher
    fun database(): CoroutineDispatcher
    fun single(): CoroutineDispatcher
}

class DefaultDispatcherProvider : CoroutinesDispatcherProvider {
    override fun main() = Dispatchers.Main
    override fun computation() = Dispatchers.Default
    override fun disk() = Dispatchers.IO
    override fun network() = Dispatchers.IO
    override fun database() = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override fun single() = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
}