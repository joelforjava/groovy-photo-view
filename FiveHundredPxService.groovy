@Grapes([
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
])
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

class FiveHundredPxService {

	private config 

	private baseUrl

	private consumerKey

	private http

	def cacheService

	FiveHundredPxService() {
		config = new ConfigSlurper().parse(Config)
		baseUrl = config.api.url
		consumerKey = config.api.key
		http = new HTTPBuilder("$baseUrl")
		cacheService = new CacheService()
		cacheService.setupCache()
	}

	def getPhotos(args) {
		def queryMap = [consumer_key:"$consumerKey", feature: 'popular', rpp: 28]
		if (args) {
			queryMap << args
		}
		def photoFeed = cacheService.loadFeedFromCache()
		if (!photoFeed) {
			photoFeed = http.get(path: "/v1/photos", contentType: JSON, query: queryMap)
			cacheService.cache(photoFeed)
		}
		photoFeed
	}

	def getPhoto(photoId, args) {
		def queryMap = [consumer_key:"$consumerKey"]
		if (args) {
			queryMap << args
		}
		def selected = cacheService.loadPhotoDetailsFromCache(photoId)
		if (!selected) {
			selected = http.get(path: "/v1/photos/$photoId", contentType: JSON, query: queryMap)
			cacheService.cachePhoto(photoId, selected)
		}
		selected
	}
}