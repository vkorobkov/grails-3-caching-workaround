package cache_test

import com.ControllerCacheable
import com.ControllerEvict
import com.SpringCacheService
import org.springframework.beans.factory.annotation.Autowired


@ControllerCacheable('betaShared')
class BetaController {

    @Autowired
    SpringCacheService smartCacheService;

    def shared() {
        System.err.println("Beta shared")

        render "Ok Beta Shared"
    }

    @ControllerCacheable('betaCustom')
    def custom() {
        System.err.println("Beta custom")

        render "Ok Beta Custom"
    }

    @ControllerEvict('betaShared')
    def evict() {
        System.err.println("Evicting beta shared")

        render "Beta shared cache has evicted"
    }

    @ControllerEvict('betaCustom')
    def evict1() {
        System.err.println("Evicting beta custom")

        render "Beta custom cache has evicted"
    }
}