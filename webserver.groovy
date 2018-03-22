@Grapes([
	@Grab('org.eclipse.jetty.aggregate:jetty-server:8.2.0.v20160908'),
	@Grab('org.eclipse.jetty.aggregate:jetty-servlet:8.2.0.v20160908'),
	@Grab('javax.servlet:javax.servlet-api:3.1.0')
])
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*
import static org.eclipse.jetty.servlet.ServletContextHandler.*

def server = new Server(1234)
def context = new ServletContextHandler(server, "/", SESSIONS)
context.resourceBase = "."
context.addServlet(DefaultServlet, "/")
context.addServlet(GroovyServlet, "*.groovy")
server.start()