<% 
import groovy.json.* 

def errorMessages = request.getAttribute('errorMessages')
def json = request.getAttribute('json')
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
	<h1>Hello</h1>
</body>