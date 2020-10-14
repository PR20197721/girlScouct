<%@page session="false"
        contentType="text/plain"
        import="org.apache.jackrabbit.JcrConstants,
                java.util.HashSet,
                java.util.Set,
                javax.jcr.Node,
                javax.jcr.Property,
                javax.jcr.Session,
                javax.jcr.Value,
                javax.jcr.query.Query,
                javax.jcr.query.RowIterator,
				org.apache.sling.jcr.api.SlingRepository" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%

    Set<String> vanityPaths = new HashSet<String>();

	SlingRepository repository = (SlingRepository)sling.getService(SlingRepository.class);
	Session session = repository.loginAdministrative(null);
	//Session session = resourceResolver.adaptTo(Session.class);
    Query query = session.getWorkspace().getQueryManager().
        createQuery(
            "/jcr:root/content//*[@sling:vanityPath]",
            Query.XPATH);
       
    RowIterator iter = query.execute().getRows();
    while (iter.hasNext()) {
        Node node = iter.nextRow().getNode();
        
        Property property = node.getProperty("sling:vanityPath");
        Value[] values;
        if (property.isMultiple()) {
            values = property.getValues();
        } else { 
            values = new Value[] { property.getValue() };
        }
        for (Value value : values) {
            try{
                String vanityPath = value.getString();
                vanityPath = vanityPath.trim();
                if (!vanityPath.startsWith("/")) {
                    vanityPath = "/" + vanityPath;
                }
                vanityPaths.add(vanityPath);
            }catch(Exception e){
            }
        }
    }
    
    for (String vanityPath : vanityPaths) {
        %><%= vanityPath %>
<%        
    }
	session.logout();
%>