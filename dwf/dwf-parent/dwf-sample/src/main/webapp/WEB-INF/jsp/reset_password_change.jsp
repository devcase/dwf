<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tu"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>Home</title>
</head>
<body>
<section>
<div class="row">
	<h1><spring:message code="label.password.change"/></h1>
	
	<div class="panel panel-default">
		<div class="panel-body">
			<form role="form" method="POST" class="form-horizontal validate">
				<sec:csrfInput/>
				<div class="form-group">
					<label class="col-sm-4 control-label "><strong><spring:message code="label.newPassword"/>*</strong> </label>
					<div class="col-sm-8">
						<input type="password" name="newPassword" required="required" class="form-control required valid">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label "><strong><spring:message code="label.newPasswordConfirmation"/>*</strong> </label>
					<div class="col-sm-8">
						<input type="password" name="newPasswordConfirmation" required="required" class="form-control required valid">
					</div>
				</div>
				<div class="text-right">
					<button type="submit" class="btn btn-primary" data-loading-text="Wait..." formaction="/resetPassword/${token}">
						Save
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
</section>
</body>
</html>
