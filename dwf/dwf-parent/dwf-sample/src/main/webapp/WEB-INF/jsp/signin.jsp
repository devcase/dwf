<!DOCTYPE html>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
<meta name="decorator" content="default" />
</head>
<body>
		<h1>Login</h1>
		<c:if test="${error}">
			<div class="alert alert-danger alert-dismissable"><spring:message code="message.login.error"/></div>
		</c:if>
		<c:if test="${logout}">
			<div class="alert alert-success alert-dismissable"><spring:message code="message.logout.success"/></div>
		</c:if>
		<dwf:editForm formaction="/signin/authenticate" buttonLabelKey="label.login">
		    <dwf:inputText name="username" />
		    <dwf:inputPassword name="password" />
		    <dwf:formGroup labelKey="label.rememberMe">
		    	<input type="checkbox" id="remember-me" name="remember-me"/>
		    </dwf:formGroup>
		    
		</dwf:editForm>
	    <a href="/resetPassword"><spring:message code="action.reset.password"/></a>
</body>
</html>
