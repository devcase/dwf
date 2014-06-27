<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<c:if test="${!empty attrMap.property}">
	<dwf:resolveEL el="${entityName}.${attrMap.property}" var="uploadKey" />
</c:if>
<h2>
	<dwf:simpleLabel textOnly="true" property="${attrMap.property}" labelKey="${attrMap.labelKey }" />
</h2>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/updateUpload/${entity.id}"/>
</c:if>

<div class="panel panel-default">
	<div class="panel-body">
		<c:if test="${!empty uploadKey}">
			<dwf:remoteUrl uploadKey="${uploadKey}" var="url" />
			<a href="${url}">${url}</a>
			<img src="${url }" class="img-responsive"/>
		</c:if>
		<form class="form-horizontal validate" method="POST" action="${formaction}" role="form" enctype="multipart/form-data">
			<input type="hidden" name="propertyName" value="${attrMap.property}"/>
			<div class="form-group ${!empty violation ?  'has-error' : ''}">
				<div class="col-sm-12">
					<input type="file" name="file" />
				</div>
			</div>

			<div class="form-group">
				<div class=" col-sm-12 text-right">
					<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait"/>" formaction="${formaction}">
						<span class="glyphicon glyphicon-floppy-disk"></span>
						<spring:message code="action.update" />
					</button>
				</div>
			</div>
		</form>
	</div>
	<!-- /.box-content -->
</div>