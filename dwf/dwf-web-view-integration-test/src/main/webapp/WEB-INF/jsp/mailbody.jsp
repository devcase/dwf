<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<h1>áááá</h1>
<p>${testmodelattribute}</p>
<p>Language: <spring:message code="test.dwf.message"/></p>
<p><dwf:locale/></p>
<p><%="THREAD: " + Thread.currentThread().hashCode() %></p>
</body>
</html>