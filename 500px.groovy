def config = new ConfigSlurper().parse(Config)

def fivehundred = new FiveHundredPxService()

def debugMode = config.debugMode

def errorMessages = []

def feed 
try {
	feed = fivehundred.getPhotos([:])
} catch(e) {
	errorMessages << 'Error loading feed'
}

def photos = fivehundred.extractImageUrlsFromFeed(feed)
def photoCount = photos?.size() ?: 0
def photoIds = fivehundred.extractImageIdsFromFeed(feed)

def selectedPhotoIndices = []

if (photoCount) {
	(0..4).each {
		// Ensure we select X unique photos
		while(true) {
			def nextIndex = new Random().nextInt(photoCount)
			if (!(nextIndex in selectedPhotoIndices)) {
				selectedPhotoIndices << nextIndex
				break
			}
		}
	}
}

def selectedPhotoIds = []
selectedPhotoIndices.each {
	selectedPhotoIds << photoIds[it]
}

def selectedPhotos = []

selectedPhotoIds.each {
	try {
		selectedPhotos << fivehundred.getPhoto(it, [:])
	} catch (e) {
		errorMessages << "Error loading photo with ID $it"
	}
}

def jsonMap = [feed : feed, photos : photos, photoCount : photoCount, photoIds : photoIds, 
			   selectedPhotoIndices : selectedPhotoIndices, selectedPhotoIds : selectedPhotoIds, 
			   selectedPhotos : selectedPhotos]

request.setAttribute('json', jsonMap)
request.setAttribute('debugMode', debugMode)
request.setAttribute('errorMessages', errorMessages)

forward 'frame.gtpl'