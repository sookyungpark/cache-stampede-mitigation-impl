package com.sookyungpark.cachestempede.per

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.LoadingCache
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.min

// this is arbitrary values
const val EXPIRATION_COEFFICIENT = 1000
const val EXPIRATION_LOG_X_MAX = 1f
const val EXPIRATION_LOG_X_MIN = 0.000001f

private val random = Random()

@Throws(java.lang.UnsupportedOperationException::class)
fun <K, V> Cache<K, V>.perGet(k: K): V? {
    return this.getIfPresent(k!!)?.let {
        val expirationDelayMillis = getExpirationDelayMillis(k)
        if (Long.MAX_VALUE == expirationDelayMillis) {
            return it
        }

        // return null if early expiration condition did met.
        val x = random.nextFloat().coerceIn(EXPIRATION_LOG_X_MIN, EXPIRATION_LOG_X_MAX)
        if (expirationDelayMillis < EXPIRATION_COEFFICIENT * -ln(x)) {
            return null
        }
        return it
    }
}

fun <K, V> Cache<K, V>.getExpirationDelayMillis(k: K): Long {
    var res = Long.MAX_VALUE
    policy().expireAfterWrite().ifPresent {
        res = min(res, it.expiresAfter.toMillis() - it.ageOf(k).get().toMillis())
    }
    policy().expireAfterAccess().ifPresent {
        res = min(res, it.expiresAfter.toMillis() - it.ageOf(k).get().toMillis())
    }
    policy().expireVariably().ifPresent {
        res = min(res, it.getExpiresAfter(k).get().toMillis())
    }
    return res
}
