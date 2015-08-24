package com;

import groovy.lang.Closure;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import javax.annotation.PostConstruct;

// It is not called CacheService due to conflict - grails already provides it's own CacheService and complains
// on my one. Lovely grails.
public class SpringCacheService {

    @Autowired
    private EhCacheCacheManager ehCacheCacheManager;

    private CacheManager manager;

    @PostConstruct
    public void init() {
        manager = ehCacheCacheManager.getCacheManager();
    }

    public void put(String cacheName, String key, Object value) {
        getCacheOrCreate(cacheName).putIfAbsent(new Element(key, value));
    }

    public<T> T get(String cacheName, String key) {
        return getFromCache(getCacheOrCreate(cacheName), key);
    }

    public<T> T getCachedOrCalculate(String cacheName, String key, Closure<T> callback) {
        // Get cache if exists or create if does not
        Cache cache = getCacheOrCreate(cacheName);

        // If result exists in cache - return it
        T value = getFromCache(cache, key);
        if (value != null) {
            return value;
        }

        // Else calculate result and put to the cache
        value = callback.call();
        cache.put(new Element(key, value));
        return value;
    }

    public void evict(String name) {
        Ehcache cache = manager.getEhcache(name);
        if (cache != null) {
            cache.flush();
        }
    }

    public void evictCache(String cacheName) {
        manager.removeCache(cacheName);
    }

    private Cache getCacheOrCreate(String cacheName) {
        Cache cache = manager.getCache(cacheName);
        if (cache == null) {
            manager.addCacheIfAbsent(cacheName);
            cache = manager.getCache(cacheName);
        }
        return cache;
    }

    public<T> T getFromCache(Cache cache, String key) {
        Element valueFromCache = cache.get(key);
        if (valueFromCache != null) {
            return (T)valueFromCache.getValue();
        }
        return null;
    }
}
