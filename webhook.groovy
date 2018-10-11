import groovy.util.logging.Slf4j
import groovy.json.*

import services.*

// import com.joelforjava.np.*

def attrNames = request.attributeNames
def headerNames = request.headerNames
def paramNames = request.parameterNames

(0..80).each { System.out.print('-') }
System.out.println()

System.out.println "Here are the attrNames: ${attrNames}"
System.out.println "Here are the headerNames: ${headerNames}"
def bufferLength
headerNames.each { headerName ->
	System.out.println "$headerName"
	def headers = request.getHeaders(headerName)
	headers.each {
		if (headerName == "Content-Length") {
			bufferLength = it
		}
		System.out.println "\t\t${it}"
	}
}
NowPlayingService service = new NowPlayingService()
SonosService sonos = new SonosService()

System.out.println("Got bufferLength of $bufferLength")
def reader = request.getInputStream()
def bytesRead
byte[] buffer = new byte[bufferLength as int]
while ((bytesRead = reader.read(buffer)) != -1) {
	System.out.println(bytesRead)
}
final String jsonString = new String(buffer)

def json

try {
	System.out.println(JsonOutput.prettyPrint(jsonString))
	json = new JsonSlurper().parseText(jsonString)
} catch (e) {
	System.err.println("Error parsing JSON: $jsonString")
	// If there is an error parsing, do we query the SONOS Server?
	// Or do we create an 'error' object to tell the NP Service to
	// query the server?
	System.out.println("Attempting to call sonos service")
	try {
		json = sonos.checkState()
	} catch(ce) {
		System.err.println("There was an error connecting to the Sonos information service")
	}
}

if (json?.data) {
	def data = json.data
	if (data.state?.currentTrack) {
		if (data.state.playbackState == "PLAYING") {
			def currentTrack = data.state.currentTrack
			System.out.println("Now Playing: ${currentTrack?.title} by ${currentTrack?.artist}")
			// def nowPlaying = new NowPlayingObject(artistName: currentTrack.artist, songTitle: currentTrack.title, album: currentTrack.album, duration: currentTrack.duration)
			service.save(JsonOutput.toJson(data.state))
		}
		if (data.state.playbackState == "STOPPED") {
			System.out.println("Playback Stopped")
			service.delete()
		}
	} else {
		service.delete()
	}
}
