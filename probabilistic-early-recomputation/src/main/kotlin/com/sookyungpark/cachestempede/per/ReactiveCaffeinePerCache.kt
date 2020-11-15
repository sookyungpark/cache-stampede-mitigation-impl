package com.sookyungpark.cachestempede.per

import com.github.benmanes.caffeine.cache.Cache
import reactor.cache.CacheMono
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import java.util.function.Function

class ReactiveCaffeinePerCache<K, V>(private val cache: Cache<K, V>) {
    fun get(key: K, func: () -> Mono<V>): Mono<V> {
        val lookupMono = Function<K, Mono<Signal<out V>>> { k ->
            Mono.justOrEmpty(cache.perGet(k))
                    .map { Signal.next(it) }
        }

        return CacheMono
                .lookup(lookupMono, key)
                .onCacheMissResume(Mono.defer(func))
                .andWriteWith { k, signal ->
                    Mono.fromRunnable<Void> {
                        if (signal.isOnError) {
                            return@fromRunnable
                        }
                        signal.get()?.let { cache.put(k, it) }
                    }
                }
    }
}