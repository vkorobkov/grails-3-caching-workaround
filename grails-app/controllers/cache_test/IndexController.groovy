package cache_test

import grails.plugin.cache.Cacheable
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class IndexController {

    @Autowired
    SmartCacheService cacheService;

    def index() {
        cacheService.doWithDogsCache("Sit", {
            System.err.println("The dog sat down")
        } )

        cacheService.doWithDogsCache("Die", {
            System.err.println("The dog is dead")
        } )

        cacheService.doWithCatsCache("Sit", {
            System.err.println("The cat sat down")
        } )

        cacheService.doWithCatsCache("Die", {
            System.err.println("The cat is dead")
        } )

        render "Ok"
    }

    def dogs() {
        cacheService.evictDogsCache()

        render "Dogs cache evicted"
    }

    def cats() {
        cacheService.evictCatsCache()

        render "Cats cache evicted"
    }
}