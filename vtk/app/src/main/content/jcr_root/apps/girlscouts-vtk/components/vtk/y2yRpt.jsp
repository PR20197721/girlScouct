

<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,java.io.*,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,org.apache.commons.beanutils.*,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
Set<String> allowedReportUsers = new HashSet<String>();
allowedReportUsers.add("005g0000002apMT");
allowedReportUsers.add("005G0000006oEkZ");
allowedReportUsers.add("005G0000006oBVG");
allowedReportUsers.add("005g0000002G004");
if( !allowedReportUsers.contains(user.getApiConfig().getUserId()) ){
    out.println("You do not have no access to this page [" + user.getApiConfig().getUserId() + "].");
    return;
} else {

        boolean isHtml= true;
        if(request.getParameter("download")!=null){
            response.setContentType("application/csv");
            isHtml = false;
        }else{
            %>
   <!--         <a href="?download=true">download report</a>
          || <a href="/content/girlscouts-vtk/controllers/vtk.ReportDetails.html">detailed report</a> -->
            <br/><br/>
            <% 
        }
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        StringBuffer buffer = new StringBuffer("2015 GS Council Report generated on " + format1.format(new java.util.Date())+ " \nCouncil, Troop, Junior, Brownie, Daisy, Total ");
        java.util.Map<String, String> cTrans = new java.util.TreeMap();     
        cTrans.put("597", "Girl Scouts of Northeast Texas"); 
        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
        cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 
        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
        cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
        cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
        cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
        cTrans.put("664", "Oregon and SW Washington");
        cTrans.put("234", "North East Ohio");
        cTrans.put("661", "Sierra Nevada");
        cTrans.put("664", "Oregon & SW Wash");
        cTrans.put("240", "Western Ohio");
        cTrans.put("607", "Arizona Cactus Pine");
        cTrans.put("536", "Kansas Heartland");
        cTrans.put("563", "Western Oklahoma");
        cTrans.put("564", "Eastern Oklahoma");
        cTrans.put("591", "San Jacinto");
        cTrans.put("636", "Northern CA");
        cTrans.put("512", "Colorado");
        cTrans.put("313", "Gateway");
        cTrans.put("212", "Kentucky Wilderness Road");
        cTrans.put("623", "San Diego");
        cTrans.put("131", "Central & Southern NJ");
        cTrans.put("263", "Western PA");
        cTrans.put("467", "Wisconsin Badgerland");
        cTrans.put("116", "Central & Western Mass");
        cTrans.put("622", "Orange County");
        cTrans.put("660", "Southern Nevada");
        cTrans.put("514", "Eastern IA & Western IL");
        cTrans.put("524", "Greater Iowa");
        cTrans.put("430", "Greater Chicago and NW  Indiana");
        
        cTrans.put("578", "Central Texas");
        cTrans.put("208", "Kentuckiana");
        cTrans.put("700", "USA Girl Scouts Overseas");
        cTrans.put("204", "Nation's Capital");
        cTrans.put("674", "Utah");
        cTrans.put("258", "Heart of Pennsylvania");
        cTrans.put("333", "Greater Atlanta");
        cTrans.put("135", "Heart of New Jersey");
        cTrans.put("289", "Black Diamond");
        cTrans.put("155", "Heart of the Hudson");
        cTrans.put("325", "Historic Georgia");
        cTrans.put("608", "Southern Arizona");
        cTrans.put("312", "Citrus");
        cTrans.put("169", "NYPENN Pathways");
        cTrans.put("596", "Greater South Texas");
        cTrans.put("583", "Texas Oklahoma Plains");
        cTrans.put("688", "Western Washington");
        cTrans.put("590", "Southwest Texas");
        cTrans.put("634", "Heart of Central California");
        cTrans.put("376", "Eastern South Carolina");
        cTrans.put("346", "Louisiana East");
        cTrans.put("117", "Eastern Massachusetts");
        cTrans.put("654", "Montana and Wyoming");
        cTrans.put("134", "Jersey Shore");
        cTrans.put("415", "Northern Illinois");
        cTrans.put("557", "New Mexico Trails");
        cTrans.put("110", "Maine");
        cTrans.put("126", "Green and White Mountains");
        cTrans.put("687", "Eastern Washington and Northern Idaho");
        cTrans.put("441", "Southwest Indiana");
        cTrans.put("238", "Ohio's Heartland");
        
        String limitRptToCouncil= request.getParameter("limitRptToCouncil");
        limitRptToCouncil= limitRptToCouncil==null ? "" :  limitRptToCouncil.trim() ;
  System.err.println("check: "+ (limitRptToCouncil.equals("")));      
        java.util.HashSet<String> ageGroups = new java.util.HashSet<String>();
        javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
        String sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '"+VtkUtil.getYearPlanBase(user, troop)+""+ (limitRptToCouncil.equals("") ? "" : (limitRptToCouncil+"/") ) + "%' and ocm_classname= 'org.girlscouts.vtk.models.Troop'";        
  sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '/vtk2015/%' and ocm_classname= 'org.girlscouts.vtk.models.Troop'";
  System.err.println(sql);     
        javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
        java.util.Map container= new java.util.TreeMap();
        javax.jcr.query.QueryResult result = q.execute();
        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
            javax.jcr.query.Row r = it.nextRow();
            String path = r.getValue("jcr:path").getString() ;
            String sfCouncil = null, sfTroopAge=null;
            try{ sfCouncil =r.getValue("sfCouncil").getString() ;}catch(Exception e){}          
            try{
                sfTroopAge= r.getValue("sfTroopAge").getString(); 
                if(!sfTroopAge.equals("2-Brownie") && !sfTroopAge.equals("3-Junior") && !sfTroopAge.equals("1-Daisy")){
                    continue;
                    }
            }catch(Exception e){}
            Integer counter = (Integer)container.get( sfCouncil+"|"+sfTroopAge );
            if( counter ==null )
                container.put(sfCouncil+"|"+sfTroopAge , new Integer(0));
            else
                container.put(sfCouncil+"|"+sfTroopAge , new Integer( counter.intValue() +1 ) );
        }
        out.println("Report Generated on "+ format1.format( new java.util.Date() ) );
        java.util.Iterator itr = container.keySet().iterator();
        while( itr.hasNext() ){
           String key = (String) itr.next();
           String councilId= key.substring(0, key.indexOf("|"));
           String ageGroup = key.substring(key.indexOf("|") +1 );
           Integer count= (Integer) container.get(key);
           out.println( (isHtml ? "<br/>" : "\n") + "\"" +cTrans.get(councilId)+"\","+ councilId +"," + ageGroup+ "," + count );          
         }
}
    %>