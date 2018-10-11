package services

@Grapes([
    @Grab(group='io.github.http-builder-ng', module='http-builder-ng-apache', version='1.0.3' )
])
import static groovyx.net.http.HttpBuilder.configure

import groovy.json.*

class SonosService {

    private conf
    private http
    
    SonosService() {
        conf = new ConfigSlurper().parse(Config)
        http = configure {
            request.uri = conf.url
        }
    }
    
    def checkState() {
        try {
            http.get(Map) {
                request.uri.path = '/state'
            }
        } catch(e) {
            return null
        }
    }

}