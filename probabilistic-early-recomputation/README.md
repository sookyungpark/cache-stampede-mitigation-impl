# PER-cache-expiry-impl
PER(Probablistic Early Recomputation) cache expiry implementation, with extended caffeine cache.

You can test the early expiration in ReactiveCaffeinePerCacheTest:
1. set cache writeExpiration for 10 seconds.
2. put 1000 different entries in cache first.
3. wait for 8 seconds.
4. search for given 1000 entries, and calculate the cache miss rate.

# References
https://en.wikipedia.org/wiki/Cache_stampede

http://cseweb.ucsd.edu/~avattani/papers/cache_stampede.pdf 
