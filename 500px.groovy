@Grapes([
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
])
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

import groovy.json.*

String baseUrl = 'https://api.500px.com'

def CONSUMER_KEY = "<<YOUR_KEY_HERE>>"

def fivehundred = new HTTPBuilder("$baseUrl")

def debugMode = false

def extractImageIdsFromFeed(feed) {
	def ids = []
	if (feed?.photos) {
		feed.photos.each {
			ids << it['id']
		}
	} else if (feed?.photo) {
		ids << feed.photo.image_url
	}
	ids
}

def extractImageUrlsFromFeed(feed) {
	def urls = []
	if (feed?.photos) {
		feed.photos.each {
			urls << it['image_url'][0]
		}
	}
	urls
}

def setupCache() {
	def dir = new File('_cache')
	if (!dir.exists()) {
		dir.mkdirs()
	}
}

def cache(feed) {
	def file = new File('_cache/_cache.json')
	def feedAsJson = JsonOutput.toJson(feed)
	if (!file.exists()) {
		file.withWriter { writer ->
			writer.write feedAsJson
		}
	}
}

def cachePhoto(photoId, details) {
	def file = new File("_cache/_${photoId}.json")
	def detailsAsJson = JsonOutput.toJson(details)
	if (!file.exists()) {
		file.withWriter { writer ->
			writer.write detailsAsJson
		}
	}
}

def loadFeedFromCache() {
	def file = new File('_cache/_cache.json')
	def feed
	if (file.exists()) {
		feed = new JsonSlurper().parseText(file.text)
		feed.loadFeedFromCache = true
		expireCache()
	}
	feed
}

def loadPhotoDetailsFromCache(photoId) {
	def file = new File("_cache/_${photoId}.json")
	def details
	if (file.exists()) {
		details = new JsonSlurper().parseText(file.text)
		details.loadedFromCache = true
	}
	details
}

private def expireCache() {
	def cacheDir = new File('_cache')
	if (cacheDir.exists()) {
		def now = new Date().time
		cacheDir.eachFile { f -> 
			if (now - f.lastModified() > (60 * 60 * 1000 * 2)) {
				f.delete()
			}
		}
	}
}

setupCache()

def errorMessages = []

def feed 
try {
	feed = loadFeedFromCache()
	if (!feed) {
		feed = fivehundred.get(path: "/v1/photos", contentType: JSON, query: [consumer_key:"$CONSUMER_KEY", feature: 'popular', rpp: 28])
		cache(feed)
	}
} catch(e) {
	errorMessages << 'Error loading feed'
}

def photos = extractImageUrlsFromFeed(feed)
def photoCount = photos?.size() ?: 0
def photoIds = extractImageIdsFromFeed(feed)

def selectedPhotoIndices = []
(0..4).each {
	selectedPhotoIndices << new Random().nextInt(photoCount)
}

def selectedPhotoIds = []
selectedPhotoIndices.each {
	selectedPhotoIds << photoIds[it]
}

def selectedPhotos = []

selectedPhotoIds.each {
	def selected
	try {
		selected = loadPhotoDetailsFromCache(it)
		if (!selected) {
			selected = fivehundred.get(path: "/v1/photos/$it", contentType: JSON, query: [consumer_key:"$CONSUMER_KEY"])
			cachePhoto(it, selected)
		}
		selectedPhotos << selected
	} catch (e) {
		errorMessages << "Error loading photo with ID $it"
	}
}

html.html {
	head {
		title '500px Popular'
		link (rel:'stylesheet', type:'text/css', href:'css/bootstrap-theme.css')
		link (rel:'stylesheet', type:'text/css', href:'css/bootstrap.css')
		link (rel:'stylesheet', type:'text/css', href:'css/main.css')
		script (type:'text/javascript', src:'https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js')
		script (type:'text/javascript', src:'js/bootstrap.js')
	}
	body  {
		nav (class: 'navbar navbar-inverse navbar-fixed-top') {
			div (class: 'container') {
				div (class: 'navbar-header') {
					button (type: 'button', class:'navbar-toggle collapsed', 'data-toggle': 'collapse', 
						    'data-target': '#navbar', 'aria-expanded': 'false', 'aria-controls': 'navbar') {
						span (class: 'sr-only', 'Toggle Navigation')
						span (class:'icon-bar')
						span (class:'icon-bar')
						span (class:'icon-bar')
					}
					a (href:'#', class: 'navbar-brand', '500px Photo Frame')
				}
				div (id: 'navbar', class: 'collapse navbar-collapse') {
					ul (class: 'nav navbar-nav') {
						li (class: 'active') {
							a(href: '500px.groovy', 'Reload Page')
						}
					}
				}
			}
		}

		div (class: 'container') {
			if (errorMessages) {
				div (class: 'row') {
					div (class: 'col-sm-12') {
						div (class: 'alert alert-danger') {
							ul {
								errorMessages.each {
									li "$it"
								}
							}
						}
					}
				}
			}
			div (class: 'row') {
				div (class: 'col-sm-11 col-sm-offset-1') {
					div (class: 'carousel slide', 'data-ride': 'carousel') {
						div (class: 'carousel-inner', role: 'listbox') {
							selectedPhotos.eachWithIndex { photo, i ->
								def css = 'item'
								if (i == 0) css = 'item active'
								div (class: "$css") {
									img (id: 'selected-photo', src: "$photo.photo.image_url")
									if (debugMode) {
										div (class: 'carousel-caption') {
											h3  "Selected photo ID: $photo.photo.id"
											p "Loaded from cache? - ${photo.loadedFromCache}"
										}
									}
								}
							}
						}
					}
				}
			}
			div (class: 'row') {
				div (class: 'col-sm-12') {
					div (class: 'panel panel-default') {
						div (class: 'panel-heading') {
							if (feed?.loadFeedFromCache) {
								div (class: 'text-success', "Found $photoCount photos loaded from cache")
							} else {
								div (class: 'text-info', "Found $photoCount photos.")
							}
						}
						div (class: 'panel-body') {
							if (photos) {
								photos.eachWithIndex { photo, i ->
									if (i in selectedPhotoIndices) {
										img (src: "$photo", class:'img-thumbnail bg-primary')
									} else {
										img (src: "$photo", class:'img-thumbnail')
									}
								}
							} else {
								div (class: 'alert alert-danger', role: 'alert', 'Problem retrieving photo feed')
							}					
						}
					}
				}
			}
			if (debugMode) {
				div (class: 'row') {
					div (class: 'col-sm-4') {
						div (class: 'panel panel-default') {
							div (class: 'panel-heading') {
								h3 (class: 'panel-title', 'Photo IDs extracted:')
							}
							div (class: 'panel-body') {
								code "${photoIds}"
							}
						}
					}
					div (class: 'col-sm-4') {
						div (class: 'panel panel-default') {
							div (class: 'panel-heading') {
								h3 (class: 'panel-title', 'Selected Photo indices extracted:')
							}
							div (class: 'panel-body') {
								code "${selectedPhotoIndices}"
							}
						}
					}
					div (class: 'col-sm-4') {
						div (class: 'panel panel-default') {
							div (class: 'panel-heading') {
								h3 (class: 'panel-title', 'Selected Photo IDs extracted:')
							}
							div (class: 'panel-body') {
								code "${selectedPhotoIds}"
							}
						}
					}
					div (class: 'col-sm-12') {
						div (class: 'panel panel-default') {
							div (class: 'panel-heading') {
								h3 (class: 'panel-title', 'Feed response from server:')
							}
							div (class: 'panel-body') {
								code "${JsonOutput.toJson(feed)}"
							}
						}
					}
					div (class: 'col-sm-12') {
						div (class: 'panel panel-default') {
							div (class: 'panel-heading') {
								h3 (class: 'panel-title', 'Selected photo response from server:')
							}
							div (class: 'panel-body') {
								code "${JsonOutput.toJson(selectedPhotos)}"
							}
						}
					}
				}
			}
		}
	}
}