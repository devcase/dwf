<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="formLayout" value="horizontal" scope="request"/>
<dwf:resolveEL el='${entityName}' var="entity"/>
<div class="modal fade" id="${attrMap.id}" tabindex="-1" role="dialog" <%-- aria-labelledby="exampleModalLabel"--%> aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">${attrMap.title}</h4>
      </div>
      <div class="modal-body">
      	<jsp:doBody></jsp:doBody>
      </div>
      <%--<div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Send message</button>
      </div> --%>
    </div>
  </div>
</div>