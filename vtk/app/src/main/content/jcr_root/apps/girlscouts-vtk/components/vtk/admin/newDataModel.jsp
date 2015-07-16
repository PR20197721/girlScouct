<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>


<table border="1">
<tr>
	<th>Troop Count
	<th>Council
	<th>Troop
	<th>Path
	<th>lastModified
	<td>Details
</tr>
<% 

java.util.List L_COUNCIL = new java.util.ArrayList();
int yp_2plus_withTime=0;

javax.jcr.Session jcr_session = (javax.jcr.Session)resourceResolver.adaptTo(javax.jcr.Session.class);

String councilId= request.getParameter("cid"); //"597";//"388";

Node n_troop_total = jcr_session.getNode(VtkUtil.getYearPlanBase(user, troop)+councilId);
long troop_total= n_troop_total.getNodes().getSize();

String sql1= "select * from nt:unstructured where jcr:path like '"+VtkUtil.getYearPlanBase(user, troop)+councilId+"/%/users/' and ocm_classname ='org.girlscouts.vtk.models.JcrNode' and jcr:lastModified is null";
javax.jcr.query.QueryManager qm1 = jcr_session.getWorkspace().getQueryManager();
javax.jcr.query.Query q1 = qm1.createQuery(sql1, javax.jcr.query.Query.SQL);
javax.jcr.query.QueryResult result1 = q1.execute();
long total_yp_no_lastModified = result1.getRows().getSize();

String sql= "select * from nt:unstructured where jcr:path like '"+VtkUtil.getYearPlanBase(user, troop)+councilId+"/%/users/' and ocm_classname ='org.girlscouts.vtk.models.JcrNode'";
javax.jcr.query.QueryManager qm = jcr_session.getWorkspace().getQueryManager();
javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
javax.jcr.query.QueryResult result = q.execute();	
int count=0;
for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext();) {
	javax.jcr.query.Row r = it.nextRow();
	javax.jcr.Value excerpt = r.getValue("jcr:path");
	String p = excerpt.getString();
	
	javax.jcr.Node t =  jcr_session.getNode( p );
	javax.jcr.NodeIterator itr= t.getNodes();
	if( t.getNodes().getSize()>1 ){
		count++;
		%>
		 
			<tr style="background-color:gray;">
			<td>
			<td> <!-- <%=L_COUNCIL.size() %>16 -->
			<td> <!-- yp_2plus_withTime=<%=yp_2plus_withTime %> -->
			<td>
			<td>
			<td>
			
		<% 
		boolean NoTs=true;
		int sched=0;
	 while( itr.hasNext()){
			Node n = (javax.jcr.Node) itr.next();
			
			String lastMod = null;
			try{ lastMod = n.getProperty("jcr:lastModified").getValue().getString(); }catch(Exception e){}
			StringTokenizer tt= new StringTokenizer(n.getPath(),"/");
			tt.nextToken();
			String council = tt.nextToken();
			String troopId = tt.nextToken();
			
			if(! L_COUNCIL.contains(council) )
				L_COUNCIL.add(council);
			
			if( lastMod!=null)
				NoTs=false;
			Node n_cust =null;
			try{ 
				n_cust = jcr_session.getNode(n.getPath()+"/lib");
			}catch(Exception e){}
			
			Node n_year =null;
			try{ 
				n_year = jcr_session.getNode(n.getPath()+"/yearPlan");
					
			}catch(Exception e){e.printStackTrace();}
			
			
			
			
		
			%>
				<tr>
					<td><%=count%>
					 <td> <%=getC(council)%> (<%=council %>)
					 <td>
					 <% try{ out.println(  n.getProperty("sfTroopName").getValue().getString() );}catch(Exception ee){}%>
					 <%=troopId%>
					<td><small><%=n.getPath() %></small> 
					<td> 
						<div style="color:green;"><%=lastMod==null ? "" : new java.util.Date( Long.parseLong(lastMod)) %></div>
						</td>
						<td>
						<%=n_cust==null ? "" : "<br/><small><font color='red'>PLAN CUSTOMIZED</font></small>" %>
						
						<%
						try{
							String xx= jcr_session.getNode(n.getPath()+"/yearPlan").getProperty("altered").getValue().getString();
							if( xx.equals("true")){
								%><div style="font-size:10px;">Plan altered: <%=xx %></div><%
							}
						}catch(Exception e){}
						
						
						  try{ 
							Node z = jcr_session.getNode(n.getPath()+"/yearPlan");
							NodeIterator z_itr = z.getNodes();
							while( z_itr.hasNext() ){
								Node z_child = (Node)z_itr.next();
								
								String cNodeName="";
								StringTokenizer t2= new StringTokenizer( z_child.getPath(), "/");
								while( t2.hasMoreElements() )
									cNodeName=t2.nextToken();
								
								if( !cNodeName.trim().equals("meetingEvents") && 
										!cNodeName.trim().equals("milestones")  ){
									%> <%=cNodeName %> , <% 
								}
							}
						  }catch(Exception e){e.printStackTrace();}
						%>
						
					</td>
					</tr>
			<%
		}
	 
	 if( NoTs ) 
		 yp_2plus_withTime++;
	}
}
%>

</table>

<table>
	<tr>
		<th>Council</th>
		<th>Troops</th>
		<th>2+ No Dates</th>
		<th>2+ Some</th>
		<th>1 YP</th>
	</tr>
	<tr>
		<td><%=getC(councilId)%> (<%=councilId %>)</td>
		<td><%=troop_total %></td>
		<td><%= yp_2plus_withTime%></td>
		<td><%=(count-yp_2plus_withTime) %></td>
		<td><%=(troop_total-count) %></td>
	</tr>
		
</table>

</br>Total Councils with problem: <%=L_COUNCIL.size() %>
</br>Year Plan with 2+ plans with NO Time: <%=yp_2plus_withTime%>
</br>Total overall troops with 2+ year plans <%=count%> 




<%!


public String getC(String code){
java.util.Map<String, String> cTrans = new java.util.TreeMap();

cTrans.put("597", "Girl Scouts of Northeast Texas"); 

cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");

cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 

cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");

cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");

cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");

cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");

return (String)cTrans.get(code);
}
%>