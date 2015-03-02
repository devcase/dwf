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

<c:if test="${!attrMap.panelless}">
<h2>
	<dwf:simpleLabel textOnly="true" property="${attrMap.property}" labelKey="${attrMap.labelKey }" />
</h2>
<div class="panel panel-default">
	<div class="panel-body">
</c:if>
		<c:if test="${!empty uploadKey}">
			<dwf:remoteUrl uploadKey="${uploadKey}" var="url" />
			<img src="${url}?ts=${timestamp}" class="img-responsive"/>
		</c:if>
		<form class="form-horizontal validate" method="POST" action="${formaction}" role="form" enctype="multipart/form-data">
			<sec:csrfInput />
			<input type="hidden" name="propertyName" value="${attrMap.property}"/>
			<div class="form-group ${!empty violation ?  'has-error' : ''}">
				<div class="col-sm-12">
					<input type="file" name="file" />
				</div>
			</div>

			<div class="form-group">
				<div class=" col-sm-12 text-right">
					<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait"/>" formaction="${formaction}">
						<spring:message code="action.update" />
					</button>
				</div>
			</div>
		</form>
<c:if test="${!attrMap.panelless}">
	</div>
	<!-- /.box-content -->
</div>
</c:if>