<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="attrMap" value="${!empty attrMap.parentAttrMap ? attrMap.parentAttrMap : attrMap}"/><%-- opção para uso em outras tags --%>
<dwf:simpleLabel var="labelText" parentAttrMap="${attrMap}"/>
<label class="col-sm-3 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}">${labelText}<strong><c:if test="${attrMap.required}">*</c:if></strong> </label>
