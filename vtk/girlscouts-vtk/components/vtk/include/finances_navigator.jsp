<div class="column large-20 medium-20 large-centered medium-centered small-24">
<%
    int currentYear = Integer.parseInt(user.getCurrentYear());
    String yearRange = user.getCurrentYear() + " - " + (currentYear + 1);
	if(isQuarterly){
		String quarterLinkTemplate = "/content/girlscouts-vtk/en/vtk.finances.html?qtr=%d";
		String prevLink = "";
		String nextLink = "";
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
				<p class="column"><% if (!prevLink.isEmpty()) { %><a class="direction prev" href="<%=prevLink%>"></a><%}%></p>
				<div class="column">
					<h3>Q<%=qtr %> <%= yearRange %></h3>
				</div>
				<p class="column"><% if (!nextLink.isEmpty()) { %><a class="direction next" href="<%=nextLink%>"></a><%}%></p>
			</div>
		<%
	} else{
		%>
			<div class="meeting-navigation row collapse">
				<p class="column"></p>
				<div class="column"><h3><%= yearRange %></h3></div>
				<p class="column"></p>
			</div>
		<%
	}
%>
</div>
