package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

@Model(adaptables = SlingHttpServletRequest.class)
public class PdfPrint {
    private String pdfPath;
    private String pageTitle;
    private String alignmentStyle;
    private boolean showPdf;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void init(){
        pdfPath = currentPage.getPath();
        pageTitle = currentPage.getTitle();
        try{
            alignmentStyle = request.getResource().adaptTo(Node.class).getProperty("alignmentcss").getString();
        }catch (Exception e){
            alignmentStyle = "";
        }
        try{
            Node content = currentPage.adaptTo(Node.class);
            content = content.getNode("jcr:content");
            if(content.hasProperty("pdfgenerator")){
                showPdf = content.getProperty("pdfgenerator").getString().equals("true");
            }else{
                showPdf = false;
            }
        }catch (Exception e){
            showPdf = false;
        }
    }
    public String getPdfPath(){
        return pdfPath;
    }
    public String getPageTitle(){
        return  pageTitle;
    }
    public String getAlignmentStyle() {
        return alignmentStyle;
    }
    public boolean getShowPdf(){return showPdf; }
}
