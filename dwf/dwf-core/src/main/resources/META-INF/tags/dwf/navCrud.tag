<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@ tag dynamic-attributes="attrMap" %>
<dwf:resolveEL el="${entityName}" var="entity"/>
<header class="navbar navbar-default navbar-fixed-top " role="banner">
	<div class="container">
	    <div class="navbar-header ">
	    	<%-- Menu button - shown when needed --%>
		    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="header .navbar-collapse">
		        <span class="sr-only">Toggle navigation</span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		    </button>

		   
		    
		    
		    <div class="navbar-brand" >
			    <%-- Back button - shown when needed --%>
			    <c:forEach items="${navCrud.items}" var="navCrudItem">
			    	<c:if test="${navCrudItem.backButton}">
			    		<a href="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}" class=" btn btn-default"><span class=" glyphicon glyphicon-chevron-left"></span>
			    		<spring:message code="${navCrudItem.labelKey }" /></a>
			    		<c:set var="backButtonFound" value="true"/>
			    	</c:if>
			    </c:forEach>
			    
			    <%-- Home button --%>
			    <c:if test="${!backButtonFound}">
			    		<a href="${appPath}/" class=" btn btn-default"><span class=" glyphicon glyphicon-home"></span>
			    		<spring:message code="action.home" /></a>
			    </c:if>
		      	<c:choose>
		      		<c:when test="${empty navCrud.entity.id}"><spring:message code="domain.${entityName}.plural" />
		      		</c:when>
		      		<c:otherwise>${navCrud.entity}</c:otherwise>
		      	</c:choose>
		    </div>
		    
		    
		    
	    </div>
	    <nav class="collapse navbar-collapse navbar-right" role="navigation">
			<ul class="nav navbar-nav"> 
				<c:forEach items="${navCrud.items}" var="navCrudItem">
					<c:choose>
						<c:when test="${navCrudItem.backButton}">
						</c:when>
					
						<c:when test="${navCrudItem.hidden}">
						</c:when>
						<c:when test="${navCrudItem.operation eq navCrud.activeOperation}"> <%-- ACTIVE OPERATION --%>
							<li class="active">
			  					<a href="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}" class="prevent-default-click" >
			  					<c:if test="${!empty navCrudItem.icon }">
			  						<span class=" glyphicon glyphicon-${navCrudItem.icon }"></span>
			  					</c:if>
				  				<spring:message code="${navCrudItem.labelKey}" />
				  				<c:if test="${!empty navCrudItem.badge}">
				  					<span class="badge">${navCrudItem.badge}</span>
				  				</c:if>
			 					</a>
			 				</li>						
						</c:when>
						<c:otherwise><%-- LINK TO OTHER OPERATION --%>
							<li>
								
			  					<a href="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}" >
			  					<c:if test="${!empty navCrudItem.icon }">
			  						<span class=" glyphicon glyphicon-${navCrudItem.icon }"></span>
			  					</c:if>
				  				<spring:message code="${navCrudItem.labelKey}" />
				  				<c:if test="${!empty navCrudItem.badge}">
				  					<span class="badge">${navCrudItem.badge}</span>
				  				</c:if>
			 					</a>
			 				</li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</nav>
	</div>
</header>
