package com.michaelflisar.toolbox.classes

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

class LazySuspend<T>(
    private val initializer: suspend () -> T,
) {

    @Volatile
    private var cachedValue: T? = null
    private val mutex = Mutex()

    suspend fun getValue(): T {
        if (cachedValue != null) return cachedValue!!
        return mutex.withLock {
            if (cachedValue == null) {
                cachedValue = initializer()
            }
            cachedValue!!
        }
    }
}