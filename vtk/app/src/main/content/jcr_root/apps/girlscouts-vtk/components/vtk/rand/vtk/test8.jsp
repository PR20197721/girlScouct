%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"
%><%@include file="/apps/myapp/core/global.jsp"

<slice:lookup var="model" type="<%=org.girlscouts.vtk.models.user.User.class%>"/>
<p>${model.id}</p>