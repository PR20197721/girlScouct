package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;

@Model(adaptables = SlingHttpServletRequest.class)
public class PrintCss {
    private static final Logger log = LoggerFactory.getLogger(PrintCss.class);
    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    private boolean addSpacing;

    @PostConstruct
    public void init(){
        try{
            NodeIterator nodeItrFirstEl = currentPage.adaptTo(Node.class).getNode("jcr:content/content/middle/par").getNodes();
            Node currNode = nodeItrFirstEl.nextNode();
            addSpacing = "girlscouts/components/image".equals(currNode.getProperty("sling:resourceType").getString());
        }catch (Exception e){
            log.error("Error parsing page components: ",e);
            addSpacing = true;
        }


    }
    public boolean getAddSpacing() {
        return addSpacing;
    }
}
