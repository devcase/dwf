<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="${locale.language}">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://dwf.devcase.com.br/decorator" prefix="decorator"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="labels" var="labelsBundle" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<dwf:dwfStyles />
<decorator:head />
</head>
<body >
	<!-- PAGE CONTAINER -->
	<div class="container">
		<div id="content">
			<dwf:userMessages />
			<decorator:body></decorator:body>
		</div>
		<!-- /#content -->
	</div><!-- /.container -->

	<dwf:dwfScripts/>
	<decorator:scripts />
	
</body>
</html>
