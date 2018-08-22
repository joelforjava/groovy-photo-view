import services.NowPlayingService

def nowPlayingService = new NowPlayingService()

if (nowPlayingService.isPlaying()) {
	forward 'MusicPlaying.groovy'
} else {
	forward 'PhotoView.groovy'
}
