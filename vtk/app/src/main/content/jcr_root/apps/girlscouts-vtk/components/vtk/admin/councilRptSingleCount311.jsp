<%@ page
        import="com.day.cq.i18n.I18n, com.day.cq.search.Predicate,  com.day.cq.search.PredicateGroup,com.day.cq.search.Query,com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.eval.FulltextPredicateEvaluator,
                 com.day.cq.search.eval.JcrPropertyPredicateEvaluator,
                 com.day.cq.search.result.Hit,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.tagging.TagManager,
                 com.day.cq.wcm.foundation.Search,
                 org.girlscouts.vtk.models.Troop,
                 org.girlscouts.web.search.DocHit,
                 javax.jcr.Node,
                 java.util.Map,
                 java.util.StringTokenizer" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp" %>
<%@include file="../admin/toolbar.jsp" %>
<h1>Council Report</h1>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js"></script>
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="https://rawgit.com/pablojim/highcharts-ng/master/src/highcharts-ng.js"></script>
<link rel="stylesheet" type="text/css" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
<%
    java.util.Map<String, String> cTrans = new java.util.TreeMap();
    cTrans.put("597", "Girl Scouts of Northeast Texas");
    cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
    cTrans.put("465", "Girl Scouts of Southeastern Michigan");
    cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
    cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
    cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
    cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
    javax.jcr.Session s = (slingRequest.getResourceResolver().adaptTo(Session.class));
    String sql = "select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '/vtk/%' and contains(*, 'org.girlscouts.vtk.ocm.TroopNode') ";
    javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
    javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
    int count = 0;
    java.util.List unCouncil = new java.util.ArrayList();
    //java.util.Map containeR= new java.util.HashMap();
    org.apache.commons.collections.MultiMap containeR = new org.apache.commons.collections.map.MultiValueMap();
    java.util.List<org.girlscouts.vtk.models.YearPlanRpt> yprs = new java.util.ArrayList<org.girlscouts.vtk.models.YearPlanRpt>();
    javax.jcr.query.QueryResult result = q.execute();
    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
        javax.jcr.query.Row r = it.nextRow();
        String path = r.getValue("jcr:path").getString();
        String sfCouncil = null, sfTroopAge = null;
        try {
            sfCouncil = r.getValue("sfCouncil").getString();
        } catch (Exception e) {
        }
        if (sfCouncil == null) {
            StringTokenizer t = new StringTokenizer(path, "/");
            t.nextToken();
            sfCouncil = t.nextToken();
        }
        try {
            sfTroopAge = r.getValue("sfTroopAge").getString();
        } catch (Exception e) {
        }
        if (sfTroopAge == null) {
            Node node = r.getNode().getNode("yearPlan/meetingEvents/").getNodes().nextNode();
            String refId = node.getProperty("refId").getString();
            String planId = refId.substring(refId.lastIndexOf("/") + 1).toLowerCase();
            if (planId.startsWith("d"))
                sfTroopAge = ("1-Daisy");
            else if (planId.startsWith("b"))
                sfTroopAge = ("2-Brownie");
            else if (planId.startsWith("j"))
                sfTroopAge = ("3-Junior");

        }
        org.girlscouts.vtk.models.YearPlanRpt ypr = new org.girlscouts.vtk.models.YearPlanRpt();
        ypr.setCouncil(sfCouncil);
        ypr.setTroop(r.getValue("sfTroopId").getString());
        ypr.setTroopName(r.getValue("sfTroopName").getString());
        ypr.setTroopAge(sfTroopAge);
        // ypr.setPath(path);
        yprs.add(ypr);
        if (!unCouncil.contains(sfCouncil))
            unCouncil.add(sfCouncil);
        containeR.put(sfCouncil, sfTroopAge);
        count++;
    }
    out.println("Total: " + count);
%>
<table>
    <tr>
        <th>Council</th>
        <th>TroopName</th>
        <th>Junior</th>
        <th>Brownie</th>
        <th>Daisy</th>
        <th>Total</th>
    </tr>
        <%
		
		for(int i=0;i<unCouncil.size();i++){
			String council= (String)unCouncil.get(i);
			
			String councilId_str = cTrans.get(council);
			
			java.util.Map <String, Integer> xx= new java.util.TreeMap();
			
			java.util.Iterator itr= containeR.keySet().iterator();
			while( itr.hasNext() ){
				String _council= (String) itr.next();
				java.util.List lvl= (java.util.List)containeR.get( council );
				if( council.equals(_council) ){
					
					for(int y=0;y<lvl.size();y++){
						String l = (String) lvl.get(y);
						if( xx.get(l)==null )
							xx.put(l, 1);
						else
							xx.put(l,  xx.get(l) +1 );
						
					}
					
				}
			}
			
			
			int total = 0, council_jun=0, council_dai=0, council_bro=0;
			if(xx.get("3-Junior")!=null ){
				total+= xx.get("3-Junior");
				council_jun=xx.get("3-Junior");
			}
			
			if( xx.get("2-Brownie") !=null){
				total += xx.get("2-Brownie");
				council_bro= xx.get("2-Brownie");
			}
			
			if( xx.get("1-Daisy") !=null){
				total += xx.get("1-Daisy");
				council_dai = xx.get("1-Daisy");
		    }
			
			%>
    <tr style="background-color:lightgray;">
        <td><%=councilId_str == null ? council : councilId_str%>
        </td>
        <td></td>
        <td><%=council_jun %>
        </td>
        <td><%=council_bro %>
        </td>
        <td><%=council_dai  %>
        </td>
        <td><%=total %>
        </td>
    </tr>
        <%
			
			int total_dai=0, total_bro=0, total_jun=0, council_total=0;
			
			
			java.util.List <String>_troops = getTroops(yprs,council);
			for(int ii=0;ii<_troops.size();ii++){
				String troopId = _troops.get(ii);
				String troopName= getTroopName( yprs, council, troopId);
				
					
				//int jun= getTroopCount( yprs, council, troopId , ("3-Junior"));
				//if( jun>1 ) jun=1;
				int jun=0;
				String junYP = getTroopYearPlan(yprs, council, troopId , ("3-Junior"), s);
				if( junYP!=null )
					jun=1;
				
				//int bro =getTroopCount( yprs, council, troopId , ("2-Brownie"));
				//if( bro>1 ) bro=1;
				int bro=0;;
				String broYP = getTroopYearPlan(yprs, council, troopId , ("2-Brownie"), s);
				if( broYP!=null )
					bro=1;
				
				//int dai=getTroopCount( yprs, council, troopId , ("1-Daisy"));
				//if( dai>1 )dai=1;
				int dai=0;
				String daiYP = getTroopYearPlan(yprs, council, troopId , ("1-Daisy"), s);
				if( daiYP!=null )
					dai=1;
				
				
				int troop_total = jun+ bro+dai;
				
				
				total_dai += dai;
				total_bro += bro;
				total_jun += jun;
				council_total += troop_total;
				if( true ){
					%>
    <tr>
        <td><%= troopId%>
        </td>
        <td><%=troopName %>
        </td>
        <td><%= jun > 0 ? junYP : "" %>
        </td>
        <td><%= bro > 0 ? broYP : ""%>
        </td>
        <td><%= dai > 0 ? daiYP : ""%>
        </td>
        <td><%= troop_total %>
        </td>
    </tr>
        <%
				}//edn if false
				
			}
			
			%>
    <tr style="background-color:lightyellow;">
        <td>*<%=councilId_str == null ? council : councilId_str%>
        </td>
        <td></td>
        <td><%=total_jun %>
        </td>
        <td><%=total_bro %>
        </td>
        <td><%=total_dai  %>
        </td>
        <td><%=council_total %>
        </td>
    </tr>
        <%
		}
		out.println("</table>" );
		
		
		
		
		
		%>
        <%!



	public java.util.Map parseData( java.util.List <Troop> users, String age){
	
		
		
		java.util.Map <String, Integer>container = new java.util.TreeMap();
		for(int i=0;i<users.size();i++){
			
			if( users.get(i).getSfTroopAge().toLowerCase().contains(age.toLowerCase()) ){
				if( !container.containsKey(age)){
					container.put(users.get(i).getSfCouncil(), 1);
		
				}else{
					
					container.put(users.get(i).getSfCouncil(), container.get(users.get(i).getSfCouncil()) +1);
					
				}
				
			}
			
		}
		return container;
			
	}


	public java.util.List doConv(java.util.Map map){
		
		java.util.List container = new java.util.ArrayList();
		java.util.Iterator itr= map.keySet().iterator();
		while( itr.hasNext())
			container.add( map.get( itr.next() ) );
		
		return container;
		
	}
	
	java.util.List unqCouncil;
	
	public java.util.Map parseData( java.util.List <Troop> users){
		
		 unqCouncil = new java.util.ArrayList();
		java.util.Map<String, Map> main = new java.util.TreeMap<String, Map>();		
		for(int i=0;i<users.size();i++){
			
			if( !unqCouncil.contains(users.get(i).getSfCouncil()) )
				unqCouncil.add(users.get(i).getSfCouncil());
			
			java.util.Map <String, Integer> container = main.get( users.get(i).getSfTroopAge() );
			if(container==null){				
				container = new java.util.TreeMap<String, Integer>();
				container.put(users.get(i).getSfCouncil(), 1);				
			}else{
				Integer curCount = container.get(users.get(i).getSfCouncil());
				if( curCount==null) curCount=0;
				//container.put(users.get(i).getSfCouncil(), container.get(users.get(i).getSfCouncil()) +1);
				container.put(users.get(i).getSfCouncil(), (curCount +1));
			}
			
			main.put(users.get(i).getSfTroopAge(), container);
			
			
		}
		return main;
			
	}
	
	private java.util.List<Troop> doFix( java.util.List<Troop> users){
		
		for(int i=0;i< users.size();i++){
		  try{
			  
			  
			if( users.get(i).getSfCouncil()==null ){
				java.util.StringTokenizer t= new java.util.StringTokenizer(users.get(i).getPath(), "/");
				t.nextToken();
				users.get(i).setSfCouncil(t.nextToken() );
			}
			
			
			
			if( users.get(i).getSfTroopAge()==null ){
				//System.err.println("testss : "+ (users.get(i).getYearPlan()==null ));
				String ref= users.get(i).getYearPlan().getMeetingEvents().get(0).getRefId();
				//System.err.println("REf: "+ ref);
			//System.err.println("test: "+ users.get(i).getRefId());	
				String planId = ref.substring( ref.lastIndexOf("/") +1).toLowerCase();
			//System.err.println( "plaI: " +planId );
				
				if( planId.startsWith("d"))
					users.get(i).setSfTroopAge("1-Daisy");
				else if( planId.startsWith("b"))
					users.get(i).setSfTroopAge("2-Brownie");
				else if( planId.startsWith("j"))
					users.get(i).setSfTroopAge("3-Junior");
			}
			//System.err.println( users.get(i).getSfTroopAge() );
		  }catch(Exception e){e.printStackTrace();}
		}
		
		return users;
	}

	
	public int getTroopCount(java.util.List<org.girlscouts.vtk.models.YearPlanRpt> container, 
			String council, String troop, String troopAge){
		
		int total =0;
		for(int i=0;i<container.size();i++){
			org.girlscouts.vtk.models.YearPlanRpt ypr = container.get(i);
			if( ypr.getCouncil().equals( council) && ypr.getTroop().equals(troop) && ypr.getTroopAge().equals(troopAge)){
				total ++;
			}
		}
		return total;
	}
	
	public java.util.List<String> getTroops( java.util.List<org.girlscouts.vtk.models.YearPlanRpt> container, 
			String council){
		
		java.util.List <String> troops =new java.util.ArrayList<String>();
		
		for(int i=0;i<container.size();i++){
			org.girlscouts.vtk.models.YearPlanRpt ypr = container.get(i);
			if( ypr.getCouncil().equals( council) && !troops.contains(ypr.getTroop())  )
				troops.add( ypr.getTroop() );
		}
		return troops;
	}
	
	private String  getTroopName(java.util.List<org.girlscouts.vtk.models.YearPlanRpt> container, String council, String troop){
		
		for(int i=0;i<container.size();i++){
			org.girlscouts.vtk.models.YearPlanRpt ypr = container.get(i);
			if( ypr.getCouncil().equals( council) && ypr.getTroop().equals(troop) )
				return ypr.getTroopName();
		}
		return "";		
	}
	
	public String getTroopYearPlan(java.util.List<org.girlscouts.vtk.models.YearPlanRpt> container, 
			String council, String troop, String troopAge, javax.jcr.Session s){
		
		String ypName="";
		for(int i=0;i<container.size();i++){
			org.girlscouts.vtk.models.YearPlanRpt ypr = container.get(i);
			if( ypr.getCouncil().equals( council) && ypr.getTroop().equals(troop) && ypr.getTroopAge().equals(troopAge)){
				try{
					Node yearPlan = s.getNode("/vtk/"+ council +"/troops/"+ troop +"/yearPlan");
					ypName= yearPlan.getProperty("name").getString();
				}catch(Exception e){e.printStackTrace();}
			}
		}
		if( ypName!=null && ypName.equals(""))
			return null;
		
		return ypName;
	}
	
%>
