<%@attribute name="targetEntity" required="true" type="java.lang.String"%>
<%@attribute name="theme" type="java.lang.String" description="theme: null, facebook or mac"%>
<%@attribute name="property" type="java.lang.String"%>
<%@taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<dwf:formGroup targetEntity="${targetEntity}" property="${property}">
	<input class="token-input" theme="${theme}" property="${property}" targetEntityName="${targetEntity}" />
	<div class="token-div" style="display: none">
		<c:forEach items="${value}" var="item">
			<input type="hidden" token-id="${item.id}" class="init-token-id" value="${item.id}" />
			<input type="hidden" token-id="${item.id}" class="init-token-name" value="${item.name}" />		
		</c:forEach>
	</div>
</dwf:formGroup>
