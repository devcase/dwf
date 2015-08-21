<%@tag import="java.text.DecimalFormatSymbols"%>
<%@tag import="java.util.Locale"%>
<%@tag import="org.springframework.context.i18n.LocaleContextHolder"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%-- 
 	Include script tags for DWF javascript and dependencies
 	Also include the scripts from a decorated page using sitemesh (FastAndDumbHTMLParser)
 	Should be placed at the end of the document so the pages load faster
--%>  
<%
//define o formato da data para uso do moment.js e do jquery-datepicker
Locale locale = LocaleContextHolder.getLocale();
String datePatternMoment;
if (locale == null) {
datePatternMoment  = "DD-MM-YYYY";
} if(locale.equals(Locale.US)) {
datePatternMoment  = "MM-DD-YYYY";
} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
datePatternMoment="YYYY-MM-DD";
} else {
datePatternMoment  = "DD-MM-YYYY";
}
getJspContext().setAttribute("datePatternMoment", datePatternMoment);

DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
char decimal = symbols.getDecimalSeparator();
char grouping = symbols.getGroupingSeparator();

getJspContext().setAttribute("decimalSeparator", decimal);
getJspContext().setAttribute("groupingSeparator", grouping);

%>
<script type="text/javascript" ><%-- Prepara variáveis usadas por scripts dwf --%>
	var $appPath = '${appPath}';
	var $datePatternMoment = '${datePatternMoment}';
	var $decimalSeparator = '${decimalSeparator}';
	var $groupingSeparator = '${groupingSeparator}';
</script>
<script type="text/javascript" src="${appPath}/resources/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/i18n/jquery.ui.datepicker-<dwf:locale/>.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.datetimepicker.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.validate/additional-methods.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.validate/localization/messages_<dwf:locale format="underscore"/>.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.validate/localization/methods_<dwf:locale format="language"/>.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/serializeObject.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.tokeninput.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/jquery.price_format.2.0.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-core.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-token-input.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-remoteload.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-remotecontents.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-paginator.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-horizontal-scroller.js"></script>
<script type="text/javascript" src="${appPath}/resources/js/dwf-autoreload.js"></script>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
