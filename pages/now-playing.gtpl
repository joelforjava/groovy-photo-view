<% 
import groovy.json.* 

def errorMessages = request.getAttribute('errorMessages')
def np = request.getAttribute('np')
def debugMode = request.getAttribute('debugMode')
%>

<!DOCTYPE html>
<html>
<head>
	<title>Popular Photos</title>
	<link rel="stylesheet" type="text/css" href="../css/bootstrap-theme.css">
	<link rel="stylesheet" type="text/css" href="../css/bootstrap.css">
	<link rel="stylesheet" type="text/css" href="../css/main.css">
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script type="text/javascript" src="../js/bootstrap.js"></script>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" 
					    data-target="#navbar" aria-expanded="false" aria-controls="navbar">
			    	<span class="sr-only">Toggle Navigation</span>
			    	<span class="icon-bar"></span>
			    	<span class="icon-bar"></span>
			    	<span class="icon-bar"></span>
				</button>
				<a href="#" class="navbar-brand">Now Playing</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active">
						<a href="MusicPlaying.groovy">Reload Music Display</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container">
		<div class="row">
			<div class="col-sm-11 col-sm-offset-1">
				<% if (np) { %>
					<div class="panel panel-default">
						<div class="panel-heading">
							<div class="panel-title">
								Now Playing
							</div>
						</div>
						<div class="panel-body">
							<div class="media">
								<% if (np.albumArtUri) { %>
									<div class="media-left">
										<img class="media-object" src="${np.albumArtUri}"/>
									</div>
								<% } %>
								<div class="media-body">
									<h3 class="media-heading">${np.songTitle}</h3>
									<h4>${np.artistName}</h4>
									<h4>From the album: ${np.album}</h4>
								</div>
							</div>
						</div>
						<% if (np.next) { %>
							<div class="panel-footer">
								Up Next: ${np.next.songTitle} by ${np.next.artistName}
							</div>
						<% } %>
					</div>
					<% if (debugMode) { %>
						<div class="well">
							<code>${ np }</code>
						</div>
					<% } %>
				<% } else { %>
					<h2>Nothing</h2>
				<% } %>
			</div>
		</div>
	</div>
</body>