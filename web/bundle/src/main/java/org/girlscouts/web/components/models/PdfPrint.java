package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class PdfPrint {
    private String pdfPath;
    private String pageTitle;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void init(){
        pdfPath = currentPage.getPath();
        pageTitle = currentPage.getTitle();
    }

    public String getPdfPath(){
        return pdfPath;
    }
    public String getPageTitle(){
        return  pageTitle;
    }
}
