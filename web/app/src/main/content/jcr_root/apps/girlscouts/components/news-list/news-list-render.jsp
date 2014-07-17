

<%
  String path=(String)request.getAttribute("path");
  String title=(String)request.getAttribute("title");
  String date=(String)request.getAttribute("date");
  String text=(String)request.getAttribute("text");
  String external_url=(String)request.getAttribute("external_url");
%>

<%if(!external_url.isEmpty()){ %>
	<div class="row">
		<div class="small-24 large-24 medium-24 columns">
			<p><a href="<%=external_url%>"><%=title%></a></p>
		</div>
	</div>  
	<div class="row">
		<div class="small-24 large-24 medium-24 columns">&nbsp;</div>
    </div>

<%}else{%>

<div class="row"> 
   <div class="small-24 large-24 medium-24 columns">
      <a href="<%=path%>.html"><%=title%></a>
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

<%} %>  