<!--
  Form 'action' component
  gs-store/post.POST.jsp 
  encrypt and store form handling
--><%@page session="false"%>
<%
%><%@page
	import="	java.io.InputStream,
				org.apache.sling.api.resource.ResourceUtil,
				org.apache.sling.api.resource.Resource,
				org.apache.sling.api.SlingHttpServletRequest,
				org.apache.sling.api.request.RequestParameter,
                com.adobe.cq.social.commons.CollabUtil,
                org.apache.sling.jcr.api.SlingRepository,
                org.apache.sling.api.resource.ValueMap,
                com.day.cq.wcm.foundation.forms.FormsConstants,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                java.util.Iterator,
                java.util.concurrent.atomic.AtomicInteger,
                java.util.Map,
                java.util.HashMap,
                javax.jcr.Session,
                javax.jcr.Node,
                javax.jcr.Property,
				javax.jcr.PropertyType,
                javax.jcr.security.Privilege,
                javax.jcr.RepositoryException,
                javax.jcr.ValueFactory,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                com.day.cq.commons.jcr.JcrUtil,
				java.lang.StringBuffer,
				org.girlscouts.web.encryption.FormEncryption,
				org.girlscouts.web.exception.GirlScoutsException,
				java.lang.Exception"%><%!

    private static final AtomicInteger uniqueIdCounter = new AtomicInteger();
    private final Logger log = LoggerFactory.getLogger(getClass());

    %><%@taglib prefix="sling"
	uri="http://sling.apache.org/taglibs/sling/1.0"%>
<%
    %><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%>
<cq:defineObjects />
<sling:defineObjects />
<%
    final SlingRepository repository = sling.getService(SlingRepository.class);
    final ValueMap props = ResourceUtil.getValueMap(resource);
 
    String path = props.get(FormsConstants.START_PROPERTY_ACTION_PATH, "");
    // create a default path if no path is specified:
    // For example if the form is at /content/geometrixx/en/formpage
    // then the default path is /content/usergenerated/content/geometrixx/en/formpage
    if ( path.length() == 0 ) {
        final String pagePath = currentPage.getPath();
        final int pos = pagePath.indexOf('/', 1);
        path = pagePath.substring(0, pos + 1) + "usergenerated/content" + pagePath.substring(pos) + "/*";
    }
    // If path ends with / or /*, compute a unique name ourselves for the node to create,
    // so that we can pass it to StartWorkflowPostProcessor. Sling might create ancestor
    // nodes of that one if they don't exist yet, so it's hard for the processor to
    // compute the right payload path without this
    for(String suffix : new String [] { "/", "/*" }) {
        if(path.endsWith(suffix)) {
            final String uniqueId = System.currentTimeMillis() + "_" + uniqueIdCounter.addAndGet(1);
            path = path.substring(0, path.length() - suffix.length() + 1) + uniqueId;
            Session userSession = slingRequest.getResourceResolver().adaptTo(Session.class);
            
            if (CollabUtil.canAddNode(userSession, path)) {
                // Create the parent node beforehand and set "formPath" and "sling:resourceType"
                // on it to allow bulk editor and forms payload summary respectively.
                final Session adminSession = repository.loginAdministrative(null);
                try {
                    final Node node = JcrUtil.createPath(path, "sling:Folder", adminSession);
                    final Node parent = node.getParent();
                    parent.setProperty("formPath", request.getParameter(FormsConstants.REQUEST_PROPERTY_FORM_START));
                    parent.setProperty("sling:resourceType", "foundation/components/form/actions/showbulkeditor");
                    /*serialize all the parameters in format:
                   	name1:value1,value2
                   	name2:value1,value2..*/
					StringBuffer sb = new StringBuffer();
                    for(Iterator<String> itr=FormsHelper.getContentRequestParameterNames(slingRequest); itr.hasNext();){
                		final String paraName=itr.next();
                        RequestParameter[] paras = slingRequest.getRequestParameters(paraName);
                        for(RequestParameter paraValue : paras){
                        	if(paraValue.isFormField()){
                        		 sb.append(paraName);//add to encription
                                 final char NAME_VALUE_SEPARATOR=30;
                                 sb.append(NAME_VALUE_SEPARATOR);
/*                         	     for(String value:getString() ){
                                 	sb.append(value);
                                     final char VALUE_SEPARATOR=31;
                                 	sb.append(VALUE_SEPARATOR);
                                 } */
                                 String value = paraValue.getString();
                                 sb.append(value);
                                 final char VALUE_SEPARATOR=31;
                             	 sb.append(VALUE_SEPARATOR);
                                 
                                 final char PARA_SEPARATOR=29;
                                 sb.append(PARA_SEPARATOR);
                        	}else{
        						String name = paraValue.getFileName();
            					InputStream stream = paraValue.getInputStream();
            					String type = paraValue.getContentType();
            					Node file = node.addNode(paraName, "nt:file");
            					
            					Node jcrContent = file.addNode(Property.JCR_CONTENT, "nt:resource");
            					
            					jcrContent.setProperty(Property.JCR_DATA, adminSession.getValueFactory().createBinary(stream));
            					jcrContent.setProperty(Property.JCR_MIMETYPE, type);
                                log.error("<==================="+name+" stored. =====================> ");

                        	}
                        								
                        }
                        
                        //store the secret to the node
                        FormEncryption fEn=sling.getService(FormEncryption.class);
                        node.setProperty("secret",fEn.encrypt(sb.toString()));
                        node.setProperty("isEncrypted","true");

                	}                
                    adminSession.save();                
                } catch (Exception e) {
                    log.error("Failed to create path or set permissions.", e);
                    
                } finally {
                    if (adminSession != null) {
	                	if (adminSession.hasPendingChanges()) {
	                        adminSession.save();
	                	}
                        adminSession.logout();
                    }
                }
            } else {
                log.error("User does not have add_node permissions on {}", path);
            }
            break;
        }
    }
    // If a redirect page was specified
    String redirect =request.getParameter(FormsConstants.REQUEST_PROPERTY_REDIRECT);
    if(redirect!=null){
    	slingResponse.sendRedirect(redirect);
    }else{   	
    	redirect=request.getHeader("referer");
    	slingResponse.sendRedirect(redirect); 
    	
    }

%>