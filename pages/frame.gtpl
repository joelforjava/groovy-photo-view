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
	<link rel="stylesheet" type="text/css" href="css/bootstrap-theme.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.js"></script>
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
				<a href="#" class="navbar-brand">Photo Carousel</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active">
						<a href="PhotoView.groovy">Reload Page</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container">
		<% if (errorMessages) { %>
			<div class="row">
				<div class="col-sm-12">
					<div class="alert alert-danger">
						<ul>
							<% errorMessages.each { %>
								<li>${it}</li>
						    <% } %>
						</ul>
					</div>
				</div>
			</div>
		<% } %>
		<div class="row">
			<div class="col-sm-11 col-sm-offset-1">
				<div class="carousel slide" data-ride="carousel">
					<div class="carousel-inner" role="listbox">
						<% json.selectedPhotos.eachWithIndex { photo, i ->
							def css = 'item'
							if (i == 0) css = 'item active'
						%>
							<div class="${css}">
								<img class="selected-photo" src="${photo.photo.image_url}"/>
								<% if (debugMode) { %>
									<div class="carousel-caption">
										<h3>Selected Photo ID: ${photo.photo.id}</h3>
										<p>Loaded from cache? - ${photo.loadedFromCache}</p>
									</div>
								<% } %>
							</div>

						<% } %>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="panel panel-default">
					<div class="panel-heading">
						<% if (json.feed.loadFeedFromCache) { %>
							<div class="text-success">
								Found ${json.photoCount} photos loaded from cache
							</div>
						<% } else { %>
							<div class="text-info">
								Found ${json.photoCount} photos
							</div>
						<% } %>
					</div>
					<div class="panel-body">
						<% if (json.photos) {
							json.photos.eachWithIndex { photo, i ->
								if (i in json.selectedPhotoIndices) { %>
									<img src="${photo}" class="img-thumbnail bg-primary"/>
								<% } else { %>
									<img src="${photo}" class="img-thumbnail"/>
								<% }
							}
						} else { %>
							<div class="alert alert-danger" role="alert">Problem retrieving photo feed</div>
						<% } %>
					</div>
				</div>
			</div>
		</div>
		<% if (debugMode) { %>
			<div class="row">
				<div class="col-sm-4">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Photo IDs extracted:</h3>
						</div>
						<div class="panel-body">
							<code>${json.photoIds}</code>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Selected Photo indices extracted:</h3>
						</div>
						<div class="panel-body">
							<code>${json.selectedPhotoIndices}</code>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Selected Photo IDs extracted:</h3>
						</div>
						<div class="panel-body">
							<code>${json.selectedPhotoIds}</code>
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Feed response from server:</h3>
						</div>
						<div class="panel-body">
							<code>${JsonOutput.toJson(json.feed)}</code>
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Selected Photo Response From Server:</h3>
						</div>
						<div class="panel-body">
							<code>${JsonOutput.toJson(json.selectedPhotos)}</code>
						</div>
					</div>
				</div>
			</div>
		<% } %>
	</div>
</body>
</html>