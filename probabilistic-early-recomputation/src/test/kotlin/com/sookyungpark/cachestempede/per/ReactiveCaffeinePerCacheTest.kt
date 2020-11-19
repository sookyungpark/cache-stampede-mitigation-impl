package com.sookyungpark.cachestempede.per

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.junit.Test
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertTrue

class ReactiveCaffeinePerCacheTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ReactiveCaffeinePerCacheTest::class.java)
    }

    @Test
    fun testHitRateBeforeTimeout() {
        val cache: Cache<String, String> = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
                .build<String, String>()

        val totalCount = AtomicInteger()
        val missCount = AtomicInteger()
        val reactivePerCache = ReactiveCaffeinePerCache(cache)

        // first put 1000 entry into the cache
        for (x in 1..1000) {
            reactivePerCache.get(x.toString()) {
                Mono.just("$x:v")
            }.subscribe()
        }

        // wait for a moment
        Thread.sleep(8000L)

        // then check cache hit
        for (x in 1..1000) {
            reactivePerCache.get(x.toString()) {
                missCount.incrementAndGet()
                Mono.just("$x:v")
            }.subscribe()
            totalCount.incrementAndGet()
        }

        logger.debug("total count: ${totalCount.get()}")
        logger.debug("hit count: ${totalCount.get() - missCount.get()}")
        logger.debug("miss count: ${missCount.get()}")

        assertTrue(missCount.get() > 0, "miss count should be bigger than 0")
    }

}