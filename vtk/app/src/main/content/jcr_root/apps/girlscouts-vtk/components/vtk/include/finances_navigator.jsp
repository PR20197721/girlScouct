<!--   <a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=1">Q1</a> 
  <a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=2">Q2</a> 
  <a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=3">Q3</a>  
  <a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=4">Q4</a>  -->
<div class="column large-20 medium-20 large-centered medium-centered small-24">
	<%
	
	
	if(isQuarterly){
		String quarterLinkTemplate = "/content/girlscouts-vtk/en/vtk.finances.html?qtr=%d";
		String prevLink = "";
		String nextLink = "";
		//If quarter is not valid or is q1 default to q1
		if(qtr <= 1 || qtr > 4 ){
			nextLink = String.format(quarterLinkTemplate, 2);
		} else if(qtr == 4){
			prevLink = String.format(quarterLinkTemplate, 3);
		} else{
			prevLink = String.format(quarterLinkTemplate, qtr - 1);
			nextLink = String.format(quarterLinkTemplate, qtr + 1);
		}
		%>
  <div class="meeting-navigation row collapse">
    <p class="column">
      <a class="direction prev" href="<%=prevLink%>"></a>
    </p>
    <div class="column">
      <h3>Q<%=qtr %> 2015</h3>
    </div>
    <p class="column">
      <a class="direction next" href="<%=nextLink%>"></a>
    </p>
  </div>
  	<% } else{ %>
  	<div class="meeting-navigation row collapse">
    
    <div class="column">
      <h3>2015</h3>
    </div>
    
  </div>
  	
  	<%} %>
</div>