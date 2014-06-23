<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<dwf:resolveEL el="${entityName}.${attrMap.property}" var="uploadKey" />
<h2>
	<dwf:simpleLabel textOnly="true" property="${attrMap.property}" />
</h2>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/update${attrMap.property.substring(0,1).toUpperCase()}${attrMap.property.substring(1)}/${entity.id}"/>

</c:if>

<div class="panel panel-default">
	<div class="panel-body">
		<c:if test="${!empty uploadKey}">
			<dwf:remoteUrl uploadKey="${uploadKey}" var="url" />
			<a href="${url}">${url}</a>
			<img src="${url }" />
		</c:if>
		<form class="form-horizontal validate" method="POST" action="${formaction}" role="form" enctype="multipart/form-data">
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