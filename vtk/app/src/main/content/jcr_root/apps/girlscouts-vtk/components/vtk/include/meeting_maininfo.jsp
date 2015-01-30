<section className="column large-20 medium-20 large-centered medium-centered" id="main-info">
  <div className="row">
    <div className="column large-17 medium-17 small-17">
      <p>{this.props.blurb}</p>
      <section>
        
        <p>
        	<%if(loc!=null){ %>
        		Location: <%=loc.getName() %> - <a href="/content/girlscouts-vtk/controllers/vtk.map.html?address=<%= loc.getAddress()%>" target="_blank"><%=loc.getAddress() %></a>
       		<%}//end if %>
        </p>
      </section>
      <section>
        <p>Category:</p>
        <p>{this.props.cat}</p>
      </section>
    </div>
    <div className="column large-7 medium-7 small-7 text-right">
      <img src={this.props.meetingGlobalId} alt="badge" />
    </div>
  </div>
</section>
