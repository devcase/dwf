<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://dwf.devcase.com.br/decorator" prefix="decorator"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<dwf:locale format="language"/>">
<fmt:setBundle basename="labels" var="labelsBundle" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<dwf:dwfStyles />
	<decorator:head />
</head>
<body>
	<nav class="navbar navbar-default  navbar-fixed-top" role="banner">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="/">DWF Sample</a>
				<ul class="nav navbar-nav">
					<li ><a href="${appPath}/baseUser/">Usuários</a></li>
					<li ><a href="${appPath}/category/">Categorias</a></li>
				</ul>
			</div>
			<sec:authorize access="isAuthenticated()">
				<nav class="collapse navbar-collapse bs-navbar-collapse">
					<ul class="nav navbar-nav navbar-right">
						<li class="dropdown">
							<a href="#" class="dropdown-toggle"
								data-toggle="dropdown" role="button" aria-expanded="false"><sec:authentication property="principal.username"/>
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu" role="menu">
								<li><a href="${appPath}/changePassword">Trocar senha</a></li>
								<li class="divider"></li>
								<li><a href="${appPath}/logout"><spring:message code="label.logout"/></a></li>
							</ul>
						</li>
					</ul>
				</nav>
			</sec:authorize>
			
		</div>
	</nav>

	<!-- PAGE CONTAINER -->
	<div class="container">
		<div id="content">
			<dwf:userMessages />
			<decorator:body></decorator:body>
		</div>
		<!-- /#content -->
	</div>
	<!-- /.container -->

	<dwf:dwfScripts />
	<decorator:scripts />

</body>
</html>
