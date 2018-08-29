// load stuff here
import services.*

NowPlayingService service = new NowPlayingService()

def nowPlaying = service.load()

request.setAttribute('np', nowPlaying)
forward 'pages/now-playing.gtpl'