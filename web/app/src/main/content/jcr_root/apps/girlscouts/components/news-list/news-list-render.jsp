

<%
  String path=(String)request.getAttribute("path");
  String title=(String)request.getAttribute("title");
  String date=(String)request.getAttribute("date");
  String text=(String)request.getAttribute("text");
  String external_url=(String)request.getAttribute("external_url");
%> 
<div class=wrapper>
<div class="row"> 
   <div class="small-24 large-24 medium-24 columns">
      <%if(!external_url.isEmpty()){ %>
      	<a href="<%=external_url%>" target="_blank"><%=title%></a>
      <%}else{ %>
      <a href="<%=path%>.html"><%=title%></a>
      <%} %>
     
   </div>
</div>

<div class="row"> 	 
	<div class="small-24 large-24 medium-24 columns">
		<%=date%>
    </div> 
</div>
<div class="row">  
	<div class="small-24 large-24 medium-24 columns">       	
		<article>
	     	<%=text %>
	    </article>
	</div>  
</div>
</div>