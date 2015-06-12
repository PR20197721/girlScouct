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
                java.util.List,
                java.util.ArrayList,
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
                org.apache.http.impl.client.CloseableHttpClient,
                org.apache.http.client.methods.HttpPost,
                org.apache.http.NameValuePair,
                org.apache.http.message.BasicNameValuePair,
                org.apache.http.client.entity.UrlEncodedFormEntity,
                org.apache.http.client.methods.CloseableHttpResponse,
				java.lang.StringBuffer,
				org.girlscouts.web.encryption.FormEncryption,
				org.girlscouts.web.exception.GirlScoutsException,
				java.lang.Exception"%><%!

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
 
    String url = props.get("formAction", "https://www.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8");
    String id = props.get(FormsConstants.START_PROPERTY_FORMID, "");
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("username", "vip"));
    nvps.add(new BasicNameValuePair("password", "secret"));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    CloseableHttpResponse response2 = httpclient.execute(httpPost);

%>
<script>
//callback handler for form submit

$("<%=id%>").submit(function(e)
{
    var postData = $(this).serializeArray();

    $.ajax(
    {
        url : <%=url%>,
        type: "POST",
        data : postData,
        success:function(data, textStatus, jqXHR) 
        {
            //data: return data from server
        },
        error: function(jqXHR, textStatus, errorThrown) 
        {
            //if fails      
        }
    });
    e.preventDefault(); //STOP default action
    e.unbind(); //unbind. to stop multiple form submit.
});
 
$("<%=id%>").submit(); //Submit  the FORM
</script>
<%
    // If a redirect page was specified
    String redirect =request.getParameter(FormsConstants.REQUEST_PROPERTY_REDIRECT);
    if(redirect!=null){
    	slingResponse.sendRedirect(redirect);
    }else{   	
    	redirect=request.getHeader("referer");
    	slingResponse.sendRedirect(redirect); 
    	
    }

%>