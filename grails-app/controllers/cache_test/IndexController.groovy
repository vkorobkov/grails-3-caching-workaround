package cache_test

import com.ControllerCacheable
import com.ControllerEvict

class IndexController {

    @ControllerCacheable('indexController')
    def index() {
        System.err.println("Index invoked")

        render "Ok"
    }

    @ControllerEvict('indexController')
    def evict() {
        System.err.println("Evicting indexController")

        render "IndexController cache has evicted"
    }
}