<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%-- 
 	Include script tags for DWF javascript and dependencies
 	Also include the scripts from a decorated page using sitemesh (FastAndDumbHTMLParser)
 	Should be placed at the end of the document so the pages load faster
--%>  
<script type="text/javascript" >
	var $appPath = '${appPath}';
</script>
<script type="text/javascript" src="${appPath}/resources/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.datetimepicker.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.validate-1.11.1.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/serializeObject.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-core.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-remoteload.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-paginator.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-horizontal-scroller.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/ckeditor/ckeditor.js"></script>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.tokeninput.js"></script>