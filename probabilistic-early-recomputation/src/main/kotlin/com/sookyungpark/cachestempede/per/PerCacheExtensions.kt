package com.sookyungpark.cachestempede.per

import com.github.benmanes.caffeine.cache.Cache
import java.util.*
import kotlin.math.ln

const val POW_10_6 = 1000000
const val EXPIRATION_COEFFICIENT = 1000
const val EXPIRATION_LOG_X_MAX = 1f
const val EXPIRATION_LOG_X_MIN = 0.0000001f

private val random = Random()

@Throws(java.lang.UnsupportedOperationException::class)
fun <K, V> Cache<K, V>.perGet(k: K): V? {
    return this.getIfPresent(k!!)?.let {
        // return null if early expiration condition did met.
        val expirationDelayMillis = this.getExpirationDelay(k) / POW_10_6
        val x = random.nextFloat().coerceIn(EXPIRATION_LOG_X_MIN, EXPIRATION_LOG_X_MAX)

        if (expirationDelayMillis < EXPIRATION_COEFFICIENT * -ln(x)) {
            return null
        }
        return it
    }
}
