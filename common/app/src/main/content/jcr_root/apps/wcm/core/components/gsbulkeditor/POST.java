package apps.wcm.core.components.gsbulkeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.AccessControlException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceResolverFactory;
import org.girlscouts.common.util.PageReplicationUtil;
import org.girlscouts.common.events.search.GSDateTime;
import org.girlscouts.common.events.search.GSDateTimeFormat;
import org.girlscouts.common.events.search.GSDateTimeFormatter;
import org.girlscouts.common.events.search.GSDateTimeZone;

import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.girlscouts.common.servlets.GSPOST;

/**
 * Servers as base for image servlets
 */
public class POST extends SlingAllMethodsServlet {
    

    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response)
            throws ServletException, IOException {
    		HtmlResponse htmlResponse = null;
    	 		SlingBindings bindings = null;
			SlingScriptHelper scriptHelper = null;
			try{
				bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
				scriptHelper = bindings.getSling();
			}catch(Exception e){
             htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                     "Could not resolve sling helper");
             htmlResponse.send(response, true);
             return;
			}
			
			if(scriptHelper == null){
				 htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Could not resolve sling helper");
	             htmlResponse.send(response, true);
	             return;
			}
			GSPOST gspost = scriptHelper.getService(GSPOST.class);
			gspost.doPost(request, response);
    }
}
