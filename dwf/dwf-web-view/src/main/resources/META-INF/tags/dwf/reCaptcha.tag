<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="dwf.web.autoconfigure.DwfWebViewAutoConfiguration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
// reCaptcha public key must be set on application.properties
	DwfWebViewAutoConfiguration cf = WebApplicationContextUtils.getWebApplicationContext(application).getBean(DwfWebViewAutoConfiguration.class);
	getJspContext().setAttribute("publickey", cf.getRecaptchaPublicKey());
%>
<%-- has to be inside form (verifies result on submit) - see dwf-core.js --%>
<dwf:formGroup parentAttrMap="${attrMap}" withoutLabel="true">
	<div class="g-recaptcha" data-sitekey="${publickey}" data-callback="reCaptchaRemoveError"></div>
<%-- text parameter is shown only if it can't find label by code (in correct language) --%>
	<span class="recaptcha-error" style="display: none"><spring:message code="label.recaptchaError" text="Please, show us you are human."></spring:message></span>
</dwf:formGroup>