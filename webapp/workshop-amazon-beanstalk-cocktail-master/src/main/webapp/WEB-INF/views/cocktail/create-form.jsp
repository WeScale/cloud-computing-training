<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Devoxx Cocktails / Create cocktail</title>

<link rel="shortcut icon" href="${cdnUrl}${pageContext.request.contextPath}/img/favicon.ico">
<link rel="icon" type="image/png" href="${cdnUrl}${pageContext.request.contextPath}/img/favicon.png">

<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" media="screen" rel="stylesheet" type="text/css" />
    <link href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css" rel="Stylesheet" type="text/css" />

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
		<c:forEach begin="1" end="${fn:length(cocktail.ingredients) + 3}" varStatus="loopStatus">
		$("input#ingredients_${loopStatus.index}_name").autocomplete({
			minLength : 2,
			source : "${pageContext.request.contextPath}/cocktail/suggest/ingredient"
		});
		</c:forEach>
	});
</script>
</head>
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
            <div class="col-md-2">
                <em>Save the cocktail before adding an image</em>
            </div>
            <form:form id="form" action="${pageContext.request.contextPath}/cocktail" method="post">
                <div class="col-md-10">
                    <div class="row">
                        <!-- DETAILS -->
                        <div class="col-md-4">
                            <fieldset>
                                <legend>Cocktail details</legend>
                                <div class="control-group">
                                    <label class="control-label" for="name">Name</label>
                                    <div class="controls">
                                        <input id="name" name="name" type="text" value="${cocktail.name}" class="span4">
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label class="control-label" for="photoUrl">Photo URL</label>
                                    <div class="controls">
                                        <input id="photoUrl" name="photoUrl" type="text" value="${cocktail.photoUrl}" class="span4">
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label class="control-label" for="sourceUrl">Source URL</label>
                                    <div class="controls">
                                        <input id="sourceUrl" name="sourceUrl" type="text" value="${cocktail.sourceUrl}" class="span4">
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label class="control-label" for="sourceUrl">Instructions</label>
                                    <div class="controls">
                                        <textarea id="instructions" name="instructions" class="span4" rows="8">${cocktail.instructions}</textarea>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                        <!-- INGREDIENTS -->
                        <div class="col-md-6">
                            <fieldset>
                                <legend>Ingredients</legend>
                                <c:forEach items="${cocktail.ingredients}" var="ingredient" varStatus="loopStatus">
                                    <div class="control-group">
                                        <div class="controls">
                                            <input name="ingredients[${loopStatus.index}].quantity" value="${ingredient.quantity}"
                                                value="${ingredient.quantity}" class="span2" /> <input
                                                name="ingredients[${loopStatus.index}].name" id="ingredients_${loopStatus.index}_name"
                                                value="${ingredient.name}" class="span2" />
                                        </div>
                                    </div>
                                </c:forEach>
                                <c:forEach begin="1" end="3" varStatus="loopStatus">
                                    <div class="control-group">
                                        <div class="controls">
                                            <input name="ingredients[${fn:length(cocktail.ingredients) + loopStatus.index}].quantity"
                                                value="" class="span2" /> <input
                                                name="ingredients[${fn:length(cocktail.ingredients) + loopStatus.index}].name"
                                                id="ingredients_${fn:length(cocktail.ingredients) + loopStatus.index}_name" value=""
                                                class="span2" />
                                        </div>
                                    </div>
                                </c:forEach>
                            </fieldset>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <div class="btn-group">
                                <button type="submit" class="btn js-btn">Save</button>
                            </div>
                        </div>
                    </div>

                </div>
            </form:form>
        </div>
    </div>
</body>
</html>


