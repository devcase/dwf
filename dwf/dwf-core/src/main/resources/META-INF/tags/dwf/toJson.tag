<%@tag import="com.google.gson.FieldAttributes"%><%@tag import="dwf.serialization.ExcludeFromSerialization"%><%@tag import="com.google.gson.ExclusionStrategy"%><%@tag import="com.google.gson.GsonBuilder"%><%@tag import="com.google.gson.Gson"%><%@attribute name="value" type="java.lang.Object"%><%
Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
	 public boolean shouldSkipClass(Class<?> clazz) {
	      return false;
	 }
     public boolean shouldSkipField(FieldAttributes f) {
	 	return f.getAnnotation(ExcludeFromSerialization.class) != null;
	 }
}).create();
out.append(gson.toJson(getJspContext().getAttribute("value")));
%>