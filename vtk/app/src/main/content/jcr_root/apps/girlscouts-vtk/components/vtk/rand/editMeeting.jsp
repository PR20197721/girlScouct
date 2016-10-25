<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<form action="" method="POST">

<input type="hidden" name="" value=""/>

<br/> Name <input type="text" name="name" id="name" value=""/>
<br/> blurb  <input type="text" name="blurb" id="blurb" value=""/>

<!-- 
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
<br/>   <input type="text" name="" id="" value=""/>
-->

<input type="submit"/>

</form>