<%@ page session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Devoxx Cocktails</title>

<link rel="shortcut icon" href="${cdnUrl}${pageContext.request.contextPath}/img/favicon.ico">
<link rel="icon" type="image/png" href="${cdnUrl}${pageContext.request.contextPath}/img/favicon.png">

<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" media="screen" rel="stylesheet" type="text/css" />
    <link href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css" rel="Stylesheet" type="text/css" />

    <!-- Le javascript -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript" ></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js" type="text/javascript" ></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" type="text/javascript"></script>
    <script type="text/javascript">
    $(document).ready(function() {
        $("input#searchCocktailByName").autocomplete({
            minLength : 2,
            source : "${pageContext.request.contextPath}/cocktail/suggest/name"
        });
        $("input#searchCocktailByIngredient").autocomplete({
            minLength : 2,
            source : "${pageContext.request.contextPath}/cocktail/suggest/ingredient"
        });
    });
</script>
<body>
    <div class="navbar navbar-default navbar-static-top navbar-inverse">
        <div class="navbar-inner">
            <div class="container">
<div class="navbar-header">                <a class="navbar-brand" href="${pageContext.request.contextPath}/"> <img alt='Devoxx France Logo' height='28'
                    src='${pageContext.request.contextPath}/img/devoxx-france-logo.jpg' width='54' />
                </a><p class="navbar-text">Cocktails</p></div>
                <ul class="nav navbar-nav">
                    <li class="active"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/cocktail/">Cocktails</a></li>
                </ul>
                <form class="navbar-form navbar-right" action="${pageContext.request.contextPath}/cocktail/">
                    <input id="searchCocktailByName" name="name" type="text" class="search-query input-medium" placeholder="Search by name">
                </form>
                <form class="navbar-form navbar-right" action="${pageContext.request.contextPath}/cocktail/">
                    <input id="searchCocktailByIngredient" name="ingredient" type="text" class="search-query input-medium"
                        placeholder="Search by ingredient">
                </form>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <div class="hero-unit">
                    <h2>A to Z cocktails list</h2>
                    <p>A to Z list of cocktail recipes.</p>
                    <p>
                        <a class="btn btn-primary btn-large" href="${pageContext.request.contextPath}/cocktail/"> Visit our cocktails! </a>
                    </p>
                </div>
            </div>
            <div class="col-md-6">
                <div class="hero-unit">
                    <h2>Add your cocktail</h2>
                    <p>Add you cocktail recipe.</p>
                    <p>
                        <a class="btn btn-primary btn-large" href="${pageContext.request.contextPath}/cocktail/create-form"> Write your
                            cocktail recipe </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>