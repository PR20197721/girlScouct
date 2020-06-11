package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class GatedContentForm {
    //todo
    private Boolean remember;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void init() {
        //todo
    }

    public Boolean getRemember() {
        return remember;
    }
}
