package cache_test

import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable

class SmartCacheService {

    @Cacheable(value = 'dogs', key = "#key")
    def doWithDogsCache(def key, callback) {
        callback()
    }

    @CacheEvict(value = 'dogs', allEntries = true)
    def evictDogsCache() {
    }


    @Cacheable(value = 'cats', key = "#key")
    def doWithCatsCache(def key, callback) {
        callback()
    }

    @CacheEvict(value = 'cats', allEntries = true)
    def evictCatsCache() {
    }
}
