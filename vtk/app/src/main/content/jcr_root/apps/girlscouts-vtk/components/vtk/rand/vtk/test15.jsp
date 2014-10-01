<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<jsp:useBean id="foo" class="org.girlscouts.vtk.models.user.User" />

<html>
<body>
<jsp:useBean id="date" class="java.util.Date" /> 
<p>The date/time is <%= date %>
  <hr/>
  
  <c:out value="testing"/>
  <c:out value="${ foo.id }"/>
  ${foo.path }
  
  
  
  <jsp:useBean id="cart" class="org.girlscouts.vtk.models.user.User"
  scope="page"/>
  <jsp:setProperty name="cart"
    property="id" value="abc"/>
 
 
 <% 
 org.girlscouts.vtk.models.user.User user= (org.girlscouts.vtk.models.user.User)session.getValue("VTK_user");
 cart=user;
 out.println("** "+ user.getId() +" : "+ cart.getId() );
 %>
 
 <hr/>
  ${ (cart==null) }
  <hr/>
  f${cart.id }
  
  
  <input type="text" value="${ cart.id}" id=""/>
 </body>
</html>