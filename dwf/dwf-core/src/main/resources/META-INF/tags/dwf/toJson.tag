<%@tag import="java.util.GregorianCalendar"%><%@tag import="com.google.gson.FieldAttributes"%><%@tag import="dwf.serialization.ExcludeFromSerialization"%><%@tag import="com.google.gson.ExclusionStrategy"%><%@tag import="com.google.gson.GsonBuilder"%><%@tag import="com.google.gson.Gson"%><%@attribute name="value" type="java.lang.Object"%><%
Object value= getJspContext().getAttribute("value");
if(value == null) {
	out.append("null");
	return;
}

if (value instanceof GregorianCalendar) {
	GregorianCalendar cal = (GregorianCalendar) value;
	
	out.append("\"");
	out.append(String.format("%04d", cal.get(GregorianCalendar.YEAR)));
	out.append("-");
	out.append(String.format("%02d", cal.get(GregorianCalendar.MONTH)+1));
	out.append("-");
	out.append(String.format("%02d", cal.get(GregorianCalendar.DAY_OF_MONTH)));
	out.append("T");
	out.append(String.format("%02d", cal.get(GregorianCalendar.HOUR_OF_DAY)));
	out.append(":");
	out.append(String.format("%02d", cal.get(GregorianCalendar.MINUTE)));
	out.append(":");
	out.append(String.format("%02d", cal.get(GregorianCalendar.SECOND)));
	out.append(".000");
	out.append("\"");
	return;
}

Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
	 public boolean shouldSkipClass(Class<?> clazz) {
	      return false;
	 }
     public boolean shouldSkipField(FieldAttributes f) {
	 	return f.getAnnotation(ExcludeFromSerialization.class) != null;
	 }
}).setPrettyPrinting().create();
out.append(gson.toJson(getJspContext().getAttribute("value")));
%>