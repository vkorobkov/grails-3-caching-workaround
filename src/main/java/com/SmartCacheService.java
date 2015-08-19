package com;

import groovy.lang.Closure;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import javax.annotation.PostConstruct;


public class SmartCacheService {

    @Autowired
    private EhCacheCacheManager ehCacheCacheManager;

    @PostConstruct
    public void enabledCaches() {
        CacheManager manager = ehCacheCacheManager.getCacheManager();
        manager.addCacheIfAbsent("dogs");
        manager.addCacheIfAbsent("cats");
    }

    @Cacheable(value = "dogs", key = "#key")
    public void doWithDogsCache(String key, Closure callback) {
        callback.call();
    }

    @CacheEvict(value = "dogs", allEntries = true)
    public void evictDogsCache() {
    }


    @Cacheable(value = "cats", key = "#key")
    public void doWithCatsCache(String key, Closure callback) {
        callback.call();
    }

    @CacheEvict(value = "cats", allEntries = true)
    public void evictCatsCache() {
    }
}
