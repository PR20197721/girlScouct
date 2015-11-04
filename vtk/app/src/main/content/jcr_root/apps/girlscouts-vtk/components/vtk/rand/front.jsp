<%@page import="javax.jcr.query.RowIterator, javax.jcr.query.*, javax.jcr.Session, org.girlscouts.vtk.models.Troop, org.girlscouts.vtk.auth.permission.*, org.girlscouts.vtk.utils.VtkUtil"%>
<%@include file="/libs/foundation/global.jsp"%>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script>
function markReported(path){
	$.ajax({
	    url: '/content/girlscouts-vtk/controllers/vtk.front1.html?rand='+Date.now(),
	    data: { 
	        path:path,         
	        a:Date.now()
	    },
	    success: function(result) {
	       
	    }
	});
}
</script>


<h1>VTK  Google API</h1>
<%
final org.girlscouts.vtk.ejb.SessionFactory sessionFactory = sling.getService( org.girlscouts.vtk.ejb.SessionFactory.class);


       java.util.List<String[]> rptList = new java.util.ArrayList<String[]>();
       Session session = null;
       try {
           session = sessionFactory.getSession();
           

          
           String sql = "";
           sql = "select sfUserId , jcr:lastModified ,  sfTroopId, sfTroopName from nt:base where jcr:path like '"+ VtkUtil.getYearPlanBase(user, troop) +"%' and ocm_classname='org.girlscouts.vtk.models.Troop' and isAnalytics is null";

           javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
           javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
           q.setLimit(1);
           QueryResult result = q.execute();

           for (RowIterator it = result.getRows(); it.hasNext();) {
               Row r = it.nextRow();
               javax.jcr.Value excerpt = r.getValue("jcr:path");
               String path = excerpt.getString();
               try{
                   javax.jcr.Value sfUserId =  r.getValue("sfUserId");
                   javax.jcr.Value lastMondif =  r.getValue("jcr:lastModified");
                   javax.jcr.Value sfTroopId =  r.getValue("sfTroopId");
                   javax.jcr.Value sfTroopName =  r.getValue("sfTroopName");
                   String rpt[] =new String[4];
                   rpt[0] = sfUserId ==null ? "" :  sfUserId.getString();
                   rpt[1] = lastMondif==null ? "" : lastMondif.getString();
                   rpt[2] = sfTroopId==null ? "" : sfTroopId.getString();
                   rpt[3] = sfTroopName==null ? "" : sfTroopName.getString();
                   out.println(rpt[0]);
                   
                   %>
                   
                   <script>
                   
(function(i,s,o,g,r,a,m){
    i['GoogleAnalyticsObject']=r;
    i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();
    a=s.createElement(o),m=s.getElementsByTagName(o)[0];
    a.async=1;
    a.src=g;
    m.parentNode.insertBefore(a,m)})
    (window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-2646810-36', 'auto', {'name': 'vtkTracker'});
    ga('vtkTracker.send', 'pageview', {
        dimension1: "<%=rpt[3]%>",
        dimension2: "<%=rpt[2]%>",
        dimension3: "<%=rpt[0]%>",
        dimension4: "testImprt",
        'hitCallback': function() {
        	markReported('<%=path%>');
           
          },
         'hitCallbackFail' : function () {
            console.log("Unable to send Google Analytics data : <%=path%>");
            
         }
        
        });

</script>
                   <%
                   
               }catch(Exception e){e.printStackTrace();}
           }
       }catch(Exception e){e.printStackTrace();
       }finally{
           sessionFactory.closeSession(session);
       }
      
java.util.Random r = new java.util.Random();
int refr= r.nextInt(5000-1000) + 1000;
%>


<script>

function refresh() {
   
	    //uncomment 2 lines auto post db to google
        /*
        window.location.reload(true);
        setTimeout(refresh, <%=refr%>);
        */
}
setTimeout(refresh, <%=refr%>);
</script>
