package org.girlscouts.web.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.*;

import javax.jcr.observation.*;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.LoginException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.ComponentContext;

import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;


@Component(immediate=true,
        service= EventListener.class)

public class ContactsUpdateListener implements EventListener{


    Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    private Session adminSession;

    private final String[] props = {"email", "jcr:title", "team", "phone", "cq:tags"};


    protected Map<String, Object> serviceParams;

    private ObservationManager observationManager;

    private ResourceResolver rr;

    @Activate
    public void activate(ComponentContext context) throws Exception {
        log.info("activating ContactsUpdateListener");
        //Initialize resource resolver and observation manager
        try {

            Map<String, Object> serviceParams = new HashMap<String, Object>();
            serviceParams.put(ResourceResolverFactory.SUBSERVICE, "writeService");
            rr = resolverFactory.getServiceResourceResolver(serviceParams);

            String[] nodeTypes = {"cq:PageContent"};
            adminSession = repository.loginService("readService",null);
            observationManager = adminSession.getWorkspace().getObservationManager();


            //Create and execute a query to create a listener for each council's contacts path
            try{
                Session session = rr.adaptTo(Session.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();

                String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/content]) AND s.[sling:resourceType]='girlscouts/components/contact-list'";
                Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
                QueryResult resultSet = query.execute();
                NodeIterator nodeItr = resultSet.getNodes();
                while(nodeItr.hasNext()){
                    try{
                        Node node = nodeItr.nextNode();
                        observationManager.addEventListener(this, Event.PROPERTY_REMOVED|Event.PROPERTY_ADDED|Event.PROPERTY_CHANGED,node.getProperty("path").getString(),true,null, nodeTypes,true);
                    } catch (Exception e){
                        log.error("ContactsUpdateListener threw an error while creating listeners", e);
                    }

                }
            }catch (Exception e){
                log.error("ContactsUpdateListener threw an error while executing query", e);
            }
        } catch (Exception e){
            log.error("unable to register session",e);
        }
    }
    @Deactivate
    public void deactivate(){
        try{
            rr.close();
        } catch (Exception e){
            log.error("Failed to close resource resolver", e);
        }

        //close all event listeners for each council site
        try{

            EventListenerIterator eventItr = observationManager.getRegisteredEventListeners();
            while(eventItr.hasNext()){
                EventListener eventListener = eventItr.nextEventListener();
                observationManager.removeEventListener(eventListener);
            }
        } catch (Exception e){
            log.error("ContactsUpdateListener threw an error when closing listeners", e);
        }

        if (adminSession != null){
            adminSession.logout();
        }
    }
    public void onEvent(EventIterator eventIterator) {
        log.info("IN EVENT");
        try {
            while (eventIterator.hasNext()){
                Event event = eventIterator.nextEvent();
                log.info("something has been added/updated : {}", event.getPath());

                boolean val = false;
                //Check to make sure property added/updates is contained in props
                String eventPath = event.getPath();
                for(String prop : props) {
                    if(eventPath.endsWith(prop)) {
                        val = true;
                    }
                }
                //Use pageManager to get Page from eventPath
                if(val){
                    PageManager pageManager = rr.adaptTo(PageManager.class);
                    Page page = pageManager.getContainingPage(eventPath);
                    populateStaffDir(page.getAbsoluteParent(1));
                }

            }
        } catch(RepositoryException e){
            log.error("Error while addressing events",e);
        }
    }
    //Parse contacts and inject into Staff Directory contact-list component node
    public void populateStaffDir(Page homepage){
        log.info("Started parsing");

        ArrayList<String> contactList = new ArrayList<>();
        NodeIterator nodeItr = null;
        Session session = rr.adaptTo(Session.class);
        try{
            //Query for contact-list component
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["+homepage.getPath()+"]) AND s.[sling:resourceType]='girlscouts/components/contact-list'";
            Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
            QueryResult resultset = query.execute();
            nodeItr = resultset.getNodes();
        }catch (Exception e){
            log.error("ContactsUpdateListener threw an error", e);
        }
        while(nodeItr.hasNext()){
            Node node = nodeItr.nextNode();
            try{
                Node contacts = rr.getResource(node.getProperty("path").getString()).adaptTo(Node.class);
                createContactString(contacts, contactList);
                log.info("Finished parsing");
                node.setProperty("contacts", contactList.toArray(new String[0]));
                node.getSession().save();
                log.info("Finished injecting contacts information to Staff-Directory's contact-list component");
            } catch (Exception e){
                log.error("ContactsUpdateListener threw an error", e);
            }

        }
    }
    //Create String of all contacts( jcr:title/email from pages under the 'Contacts' page.
    public void createContactString(Node parent, ArrayList<String> contacts){
        StringBuilder sb = new StringBuilder();
        //recursively iterate through nodes under Contacts, append contact information to String builder
        // and add each persons contact info to the contacts ArrayList
        try{
            if(parent.hasNodes()){
                NodeIterator nodeItr = parent.getNodes();
                while(nodeItr.hasNext()){
                    Node node = nodeItr.nextNode();
                    createContactString(node, contacts);
                }
            } else{
                if(parent.hasProperty("jcr:title")){
                    if(!"Contacts".equals(parent.getProperty("jcr:title").getString())){

                        if(!parent.hasProperty("email")){
                            if(!sb.toString().equals("")){
                                sb.delete(sb.length()-2, sb.length());
                                sb.append("; ");
                            }
                            sb.append(parent.getProperty("jcr:title").getString().toUpperCase());
                            sb.append(": ");
                        }
                        if(parent.hasProperty("email")){
                            sb.append(parent.getProperty("jcr:title").getString());
                            sb.append(" : ");
                        }
                    }
                }
                if(parent.hasProperty("phone")){
                    sb.append(parent.getProperty("phone").getString());
                    sb.append(" : ");
                }
                if(parent.hasProperty("email")){
                    sb.append(parent.getProperty("email").getString());
                    sb.append(" : ");
                }
                if(parent.hasProperty("team")){
                    sb.append(parent.getProperty("team").getString());
                    sb.append(" : ");
                }
                if(parent.hasProperty("cq:tags")){
                    sb.append(parent.getProperty("cq:tags").getString());
                }
                if(parent.hasProperty("jcr:title")){
                    if(!"Contacts".equals(parent.getProperty("jcr:title").getString()))
                        contacts.add(sb.toString());
                }

            }
        } catch(Exception e){
            log.error("ContactsUpdateListener threw an error parsing contacts", e);
        }
    }
}