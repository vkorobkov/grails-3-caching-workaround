import com.ControllerEvict
import com.ControllerCacheable
import com.SpringCacheService
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value


@Log4j
class CacheInterceptor {

    @Autowired
    private SpringCacheService springCacheService

    @Autowired
    @Value('${grails.cache.enabled:true}')
    private boolean cachingIsEnabled = true

    CacheInterceptor() {
        matchAll()
    }

    boolean before() {
        // If caching is disabled in settings - skip it
        if (!cachingIsEnabled) {
            return true
        }

        def cachingAttributes = gatherRequestAttributes()
        if (!cachingAttributes.controller || !cachingAttributes.action || !cachingAttributes.cacheable) {
            return true
        }

        def cachedData = springCacheService.get(cachingAttributes.cacheable, cachingAttributes.key)
        if (cachedData != null) {
            cachedData.headers.each({ response.setHeader(it.key, it.value) })
            render(status: cachedData.status, file: cachedData.data, contentType: cachedData.headers.'Content-Type')
            return false
        }

        true
    }

    boolean after() {
        // If caching is disabled in settings - skip it
        if (!cachingIsEnabled) {
            return true
        }

        def cachingAttributes = gatherRequestAttributes()
        if (!cachingAttributes.controller || !cachingAttributes.action) {
            return true
        }

        if (cachingAttributes.evict) {
            springCacheService.evictCache(cachingAttributes.evict)
        }
        else if (cachingAttributes.cacheable) {
            putResponseToCache(cachingAttributes)
        }

        true
    }

    void afterView() {
        // no-op
    }

    private def putResponseToCache(cachingAttributes) {
        def status = response.status

        // Don't cache errors - they could be fixed(like network issues) but app will return error from cache
        if (status != 200 && status != 404) {
            return
        }

        def data = [
                data: getResponsePayload(),
                headers: response.headerNames.collectEntries { name -> [name, response.getHeader(name)] },
                status: response.status
        ]
        springCacheService.put(cachingAttributes.cacheable, cachingAttributes.key, data)

    }

    private def getResponsePayload() {
        def chunk = response.writer.out.outputChunk
        if (chunk.length) {
            (byte[])chunk.buff[chunk.start..chunk.end - 1].toArray()
        }
        else {
            new byte[0]
        }
    }

    private def gatherRequestAttributes() {
        def result = [:]

        def attributes = request?.request?.attributes
        if (!attributes) {
            return result
        }

        // Get controller name, class and action and fill the key
        result.controller = attributes.'org.grails.CONTROLLER_NAME_ATTRIBUTE'
        result.action = attributes.'org.grails.ACTION_NAME_ATTRIBUTE'
        result.key = "${result.action} - ${params}"

        // Gather annotations
        def controllerClass = attributes.'org.grails.CONTROLLER'?.class ?: attributes.'org.grails.GRAILS_CONTROLLER_CLASS'?.clazz
        if (controllerClass && result.action) {
            def method = controllerClass.getMethod(result.action)
            if (method == null) {
                log.warn("Can not get method ${result.action} of controller ${result.controller} to grab caching annotations.")
                return result
            }
            result.cacheable = method.getAnnotation(ControllerCacheable)?.value()?: controllerClass.getAnnotation(ControllerCacheable)?.value()
            result.evict = method.getAnnotation(ControllerEvict)?.value()
        }

        result
    }
}
