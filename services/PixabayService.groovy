package services

@Grapes([
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
])
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

class PixabayService {
	private config 

	private baseUrl

	private consumerKey

	private http

	PixabayService() {
		config = new ConfigSlurper().parse(Config)
		baseUrl = config.service.pixabay.api.url
		consumerKey = config.service.pixabay.api.key
		http = new HTTPBuilder("$baseUrl")
		// cacheService = new CacheService()
		// cacheService.setupCache()
	}

	def getPhotos(args) {
		def queryMap = [key:"$consumerKey", order: 'popular', editors_choice: 'false', image_type: 'photo', per_page: 28]
		if (args) {
			queryMap << args
		}
		def photoFeed = http.get(contentType: JSON, query: queryMap)
		photoFeed
	}

	def getPhoto(photoId, feed) {
		def photo = [:]
		if (feed?.hits) {
			def hit = feed.hits.find { it.id == photoId }
			if (hit) {
				photo.photo = [:]
				photo.photo['id'] = hit['id']
				photo.photo['image_url'] = hit['largeImageURL']
			}
		}
		photo
	}

	def extractImageIdsFromFeed(feed) {
		def ids = []
		if (feed?.hits) {
			feed.hits.each {
				ids << it['id']
			}
		}
		ids
	}

	def extractImageUrlsFromFeed(feed) {
		def urls = []
		if (feed?.hits) {
			feed.hits.each {
				urls << it['previewURL']
			}
		}
		urls
	}

}