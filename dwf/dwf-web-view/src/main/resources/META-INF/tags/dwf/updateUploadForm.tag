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
	
	
	<input type="hidden" name="propertyName" value="${attrMap.property}"/>
	<dwf:formGroup labelKey="label.file">
		<input type="file" name="file" class="btn btn-default"  accept="image/*"/>
	</dwf:formGroup>
	<c:if test="${!empty uploadKey and (attrMap.showImage ne false)}">
		<div class="row">
			<div class="col-xs-offset-2 col-xs-8 col-sm-offset-3 col-sm-6 col-md-offset-4 col-md-4 col-lg-offset-5 col-lg-2">
				<dwf:remoteUrl uploadKey="${uploadKey}" var="url" />
				<img src="${url}?ts=${timestamp}" class="img-responsive img-thumbnail"/>
			</div>
		</div>
	</c:if>
</dwf:form>