<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<c:if test="${!empty attrMap.property}">
	<dwf:resolveEL el="${entityName}.${attrMap.property}" var="uploadKey" />
</c:if>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/updateUpload/${entity.id}"/>
</c:if>
<dwf:simpleLabel textOnly="true" property="${attrMap.property}" labelKey="${attrMap.labelKey }" var="title"/>
<dwf:form formaction="${formaction}" enctype="multipart/form-data" parentAttrMap="${attrMap}" title="${title }">
	<c:if test="${!empty uploadKey}">
		<dwf:formGroup label="none">
			<dwf:remoteUrl uploadKey="${uploadKey}" var="url" />
			<img src="${url}?ts=${timestamp}" class="img-responsive"/>
		</dwf:formGroup>
	</c:if>
	<input type="hidden" name="propertyName" value="${attrMap.property}"/>
	<dwf:formGroup labelKey="label.file">
		<input type="file" name="file" class="btn btn-default"  accept="image/*"/>
	</dwf:formGroup>
</dwf:form>