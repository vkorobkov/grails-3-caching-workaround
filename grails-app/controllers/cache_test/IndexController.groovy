package cache_test

import com.SmartCacheService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class IndexController {

    @Autowired
    SmartCacheService smartCacheService;

    def index() {
        smartCacheService.doWithDogsCache("Sit", {
            System.err.println("The dog sat down")
        } )

        smartCacheService.doWithDogsCache("Die", {
            System.err.println("The dog is dead")
        } )

        smartCacheService.doWithCatsCache("Sit", {
            System.err.println("The cat sat down")
        } )

        smartCacheService.doWithCatsCache("Die", {
            System.err.println("The cat is dead")
        } )

        render "Ok"
    }

    def dogs() {
        smartCacheService.evictDogsCache()

        render "Dogs cache evicted"
    }

    def cats() {
        smartCacheService.evictCatsCache()

        render "Cats cache evicted"
    }
}