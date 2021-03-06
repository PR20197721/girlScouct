<%@ page import="com.day.cq.search.Query, java.util.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="include/session.jsp" %>
<%
    Logger vtklog = LoggerFactory.getLogger(this.getClass().getName());
    //Permission based on SF user_id
    Set<String> allowedReportUsers = new HashSet<String>();
    allowedReportUsers.add("005G0000006oBVG");// GS Vanessa
    if (!allowedReportUsers.contains(user.getApiConfig().getUserId())) {
        out.println("You do not have no access to this page [" + user.getApiConfig().getUserId() + "].");

    } else {
        out.println("Inconsistancy report started on " + new java.util.Date() + ". Wait for completed time");
        List<String> sqls = new ArrayList();
        //check all meetings
        sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '" + VtkUtil.getYearPlanBase(user, selectedTroop) + "') and NAME(parent)='meetingEvents' and child.ocm_classname is null");
        //check all activities
        sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '" + VtkUtil.getYearPlanBase(user, selectedTroop) + "') and NAME(parent)='activities' and child.ocm_classname is null");
        //sched
        sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '" + VtkUtil.getYearPlanBase(user, selectedTroop) + "') and NAME(child)='schedule' and  (child.dates is null or child.dates ='')");
        javax.jcr.Session s = null;
        try {
            s = resourceResolver.adaptTo(javax.jcr.Session.class);
            javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = null;
            for (String sql : sqls) {
                try {
                    out.println("<hr/>" + sql);
                    q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
                    javax.jcr.query.QueryResult result = q.execute();
                    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                        javax.jcr.query.Row r = it.nextRow();
                        String path = r.getPath();
                        out.println("<div style='color:red; padding-top:25px;'>" + path + "</div>");
                    }//end for
                } catch (Exception e) {
                    out.println("<br/><br/>FOUND ERROR");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            vtklog.error("Error Occurred: ", e);
        }
    }//edn else
    out.println("<br/><br/>Inconsistancy report completed on " + new java.util.Date());
%>