package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class PdfPrint {
    private String pagePath;
    private String pageTitle;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void init(){
        pagePath = currentPage.getPath() + ".pdfrender.html";
        pageTitle = currentPage.getTitle();
    }

    public String getPagePath(){
        return pagePath;
    }
    public String getPageTitle(){
        return  pageTitle;
    }
}
