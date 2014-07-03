<%@ tag dynamic-attributes="attrMap"%>
<c:set var="decorator" value="${empty decorator || attrMap.force ? attrMap.decorator: decorator}"/>