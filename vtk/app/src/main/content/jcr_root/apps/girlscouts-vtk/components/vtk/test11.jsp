<%@page import="org.alex.app.core.*"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@include file="/apps/alex/core/global.jsp"%>


<%
// new org.alex.app.core.Main().test();
%>

<slice:lookup var="model" type="<%=TextModel.class%>"/>
<p>${model.text}, ${model.pageTitle} -${model.path}</p>

