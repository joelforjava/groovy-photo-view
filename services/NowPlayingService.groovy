package services

import groovy.json.*
import groovy.transform.ToString

import java.util.concurrent.TimeUnit

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
		if (data) {
			if (data.currentTrack) {
				def currentTrack = data.currentTrack
				System.out.println("Now Loading: ${currentTrack?.title} by ${currentTrack?.artist}")
				nowPlaying = new NowPlaying(artistName: currentTrack.artist, songTitle: currentTrack.title, album: currentTrack.album, duration: currentTrack.duration, albumArtUri: currentTrack.absoluteAlbumArtUri,
					elapsedTime: data.elapsedTime, elapsedDisplay: data.elapsedTimeFormatted)
			}
			if (data.nextTrack) {
				def nextTrack = data.nextTrack
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
	long duration
	long elapsedTime
	String durationDisplay
	String elapsedDisplay
	String albumArtUri
	NowPlaying next

	void setDuration(value) {
		this.duration = value
		def secs = value
		def millis = secs * 1000
		def hours =  TimeUnit.MILLISECONDS.toHours(millis)
		def minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)
		def seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)

		this.durationDisplay = new Formatter().format('%02d:%02d:%02d', Math.abs(hours), Math.abs(minutes), Math.abs(seconds))
	}
}