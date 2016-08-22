    <div id="combineMeetings" class="column small-24 large-10 medium-10">
   
    <form action="">
    
        <input type="hidden" name="midsToCombine" value="<%=request.getParameter("mids")%>"/>
        
        <%
        String mids = request.getParameter("mids");
        java.util.List <org.girlscouts.vtk.models.MeetingE>meetingsToCancel = org.girlscouts.vtk.ejb.meetingUtil.getMeetingToCancel(user, troop);
        for(int i=0;i<meetingsToCancel.size();i++) {
         
         java.util.StringTokenizer t= new java.util.StringTokenizer(mids, ",");
         while( t.hasMoreElements()){
	         String mid= t.nextToken();
	         if( mid.equals(meetingsToCancel.get(i).getUid())){
	            %> <%= meetingsToCancel.get(i).getMeetingInfo().getName()%> <% 
	         }//end if
         }//edn wihle
                                
         }//edne for
        
        
        %>
        
        
        
        **<%=request.getParameter("mids")%>
        
        <input type="button" value="Cancel" />
        <input type="button" value="Back" />
        <input type="button" value="Save" />
        
    </form>
   
   </div>