
import groovy.json.*

class CacheService {

	private static final String CACHE_DIR_NAME = '_cache'

	private static final String MAIN_CACHE_FILE_NAME = '_cache.json'

	private static final int TWO_HOURS = 60 * 60 * 1000 * 2

	private def jsonSlurper = new JsonSlurper()

	def setupCache() {
		def dir = new File(CACHE_DIR_NAME)
		if (!dir.exists()) {
			dir.mkdirs()
		}
	}

	def cache(feed) {
		def file = new File("$CACHE_DIR_NAME/$MAIN_CACHE_FILE_NAME")
		def feedAsJson = JsonOutput.toJson(feed)
		if (!file.exists()) {
			file.withWriter { writer ->
				writer.write feedAsJson
			}
		}
	}

	def cachePhoto(photoId, details) {
		def file = new File("$CACHE_DIR_NAME/_${photoId}.json")
		def detailsAsJson = JsonOutput.toJson(details)
		if (!file.exists()) {
			file.withWriter { writer ->
				writer.write detailsAsJson
			}
		}
	}

	def loadFeedFromCache() {
		def file = new File("$CACHE_DIR_NAME/$MAIN_CACHE_FILE_NAME")
		def feed
		if (file.exists()) {
			feed = jsonSlurper.parseText(file.text)
			feed.loadFeedFromCache = true
			expireCache()
		}
		feed
	}

	def loadPhotoDetailsFromCache(photoId) {
		def file = new File("$CACHE_DIR_NAME/_${photoId}.json")
		def details
		if (file.exists()) {
			details = jsonSlurper.parseText(file.text)
			details.loadedFromCache = true
		}
		details
	}

	private def expireCache() {
		def cacheDir = new File(CACHE_DIR_NAME)
		if (cacheDir.exists()) {
			def now = new Date().time
			cacheDir.eachFile { f -> 
				if (now - f.lastModified() > TWO_HOURS) {
					f.delete()
				}
			}
		}
	}

}