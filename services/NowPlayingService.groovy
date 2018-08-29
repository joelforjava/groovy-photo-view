package services

import groovy.json.*
import groovy.transform.ToString

class NowPlayingService {

	private static final String FILE_NAME = 'now_playing'
	private static final String FILE_LOCATION = System.getProperty('java.io.tmpdir')

	def isPlaying() {
		new File(FILE_LOCATION, FILE_NAME).exists()
	}
	
	def save(String jsonString) {
		if (!jsonString) {
			System.out.println('String was null. Returning.')
			return
		}

		new File(FILE_LOCATION, FILE_NAME).withWriter { writer ->
			writer.write jsonString
		}
	}

	def load() {
		File file = new File(FILE_LOCATION, FILE_NAME)
		if (!file.exists()) {
			System.out.println("$FILE_NAME was not found.")
			return null
		}

		def array = file as String[]
		def jsonString = array.join('').trim()
		def json = new JsonSlurper().parseText(jsonString)

		if (json) {
			System.out.println("JSON was loaded from file.")
			System.out.println(json)
		}

		convertJsonToObject(json)
	}

	def delete() {
		File file = new File(FILE_LOCATION, FILE_NAME)
		if (file.exists()) file.delete()
	}

	private convertJsonToObject(data) {
		def nowPlaying
		if (data?.data?.state) {
			def currentState = data.data.state
			if (currentState.currentTrack) {
				def currentTrack = currentState.currentTrack
				System.out.println("Now Loading: ${currentTrack?.title} by ${currentTrack?.artist}")
				nowPlaying = new NowPlaying(artistName: currentTrack.artist, songTitle: currentTrack.title, album: currentTrack.album, duration: currentTrack.duration, albumArtUri: currentTrack.absoluteAlbumArtUri)
			}
			if (currentState.nextTrack) {
				def nextTrack = currentState.nextTrack
				nowPlaying.next = new NowPlaying(artistName: nextTrack.artist, songTitle: nextTrack.title, album: nextTrack.album, duration: nextTrack.duration, albumArtUri: nextTrack.absoluteAlbumArtUri)
			}
		}
		nowPlaying
	}
}

@ToString
class NowPlaying {
	String artistName
	String songTitle
	String album
	BigInteger duration
	String albumArtUri
	NowPlaying next
}