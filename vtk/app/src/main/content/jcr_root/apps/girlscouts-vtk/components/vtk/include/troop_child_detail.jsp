<dd class="accordion-navigation clearfix">
  <div id="panel<%=i+1%>b" class="content clearfix">
    <ul class="column large-4">
             <li>DOB:
                            <%
                            String mike= contact.getDob();
                        //System.err.println("alexError ********* "+ mike ); 
                            
                            if( mike != null ){
                            	try{
                            		//FORMAT_MMddYYYY.format(fmt_yyyyMMdd.parse(mike))
                            	     %><%=  mike %><%
                            	}catch(Exception e){System.err.println("alexError: |"+mike+"|");e.printStackTrace();}
                                    
                            }else{
                            	//System.err.println("alexError: na");
                                %>N/A<%
                            }
                            %>
            </li>                
      <li>AGE: <%=contact.getAge() %></li>
    </ul>
    <ul class="column large-18 right">
      <li><address><p><%=contact.getAddress() %><br/><%=contact.getCity() %>, <%=contact.getState() %><br/><%=contact.getZip() %></p></address></li>
    </ul>
     <ul class="column large-18">
     
     <%
     if( contact.getContacts()!=null )
      for(Contact contactSub: contact.getContacts()){ %>
       <li class="row">                           
          <p><strong>Secondary Info:</strong></p>
          <div class="row">
            <span class="column large-5"><%=contactSub.getFirstName() %> <%=contactSub.getLastName() %></span>
                              <a class="column large-14 email" href="mailto:<%=contactSub.getEmail()%>"><i class="icon-mail"></i><%=contactSub.getEmail() %></a>
            <span class="column large-5"><%=contactSub.getPhone()==null ? "" : contactSub.getPhone() %></span>
          </div>
        </li>
     <%} %>
      <li class="row">
        <p><strong>Achievements:</strong></p>
        <p>
          <%
          boolean isFirstItem = true;
          for(int y=0;y<infos.size();y++) {
            if(infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING) {
            if (!isFirstItem) {
                out.println(",");
            }
              out.println(((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getName());

              isFirstItem = false;
            }
          }
          %>
          </p>
         </li>
         <li class="row">
          <p><strong>Meetings Attended:</strong></p>
          <p>
          <% for(int y=0;y<infos.size();y++) {
              if(infos.get(y).isAttended()) {
                out.println(fmr_ddmm.format(sched_bm_inverse.get( infos.get(y).getYearPlanComponent())));
                out.println((infos.size() > 1 && infos.size()-1 !=y) ? "," : "");
              }
          } %>
          </p>
        </li>                          
     </ul>
  </div>
</dd>


