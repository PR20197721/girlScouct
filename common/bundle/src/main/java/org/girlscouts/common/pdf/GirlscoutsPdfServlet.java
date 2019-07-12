package org.girlscouts.common.pdf;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.io.font.woff2.Woff2Converter;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.text.BaseColor;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.jcr.Node;
import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

@SlingServlet(
        label = "Girl Scouts PDF Servlet", description = "Generate PDF from Girl Scouts site page", paths = {},
        methods = {"POST"}, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "girlscouts-common/servlet/page-pdf" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class GirlscoutsPdfServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(GirlscoutsPdfServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {
            response.setContentType("application/pdf");
            ByteArrayOutputStream bais = new ByteArrayOutputStream();

            StringBuilder sb = new StringBuilder();
            Resource rsrc;
            try {
                rsrc = request.getResourceResolver().resolve(request.getParameter("path").replaceAll("/jcr:content", ""));
            }catch (Exception e){
                rsrc = request.getResourceResolver().resolve("/content/gsusa/en");
            }
            Page homepage = rsrc.adaptTo(Page.class).getAbsoluteParent(2);
            Node home = homepage.getContentResource().adaptTo(Node.class);
            //Get header path
            String path = "";
            try{
                if(home.hasNode("header/logo/regular")){
                    path = home.getNode("header/logo/regular").getProperty("fileReference").getString();
                }else{
                    path = home.getNode("header/logo/image").getProperty("fileReference").getString();
                }
            }catch (Exception e){
                path = "/content/dam/girlscouts-gsusa/images/logo/logo.png.transform/cq5dam.web.1280.1280/img.png";
            }
            String title = "";
            try{
                title = request.getParameter("title") != null ? request.getParameter("title") : homepage.getTitle();
            }catch(Exception e){
                log.error("Error parsing title, using homepage title: ", e);
                title = homepage.getTitle();
            }
            buildHtml(sb, request);
            generatePdf(bais, sb.toString(), path, title, request, request.getResourceResolver());
            response.setContentLength(bais.size());
            bais.writeTo(response.getOutputStream());
            bais.flush();
            response.flushBuffer();
        }catch (Exception e){
            log.error("Error generating pdf:",e);
        }

    }

    /** OptingServlet Acceptance Method **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }
    public void buildHtml(StringBuilder sb, SlingHttpServletRequest request) {
        String pageHtml;
        try{
            pageHtml = URLDecoder.decode(request.getParameter("pageHtml"),StandardCharsets.UTF_8.name());
        }catch (Exception e){
            log.error("Error decoding request html parameter: ",e);
            pageHtml = request.getParameter("pageHtml");
        }
        sb.append(pageHtml);
    }

    public Document generatePdf(ByteArrayOutputStream outputStream, String html, String path, String title, SlingHttpServletRequest request, ResourceResolver resourceResolver) throws IOException {
        WriterProperties writerProperties = new WriterProperties();

        PdfWriter pdfWriter = new PdfWriter(outputStream, writerProperties);

        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        //Set meta tags
        PdfDocumentInfo pdfMetaData = pdfDoc.getDocumentInfo();
        pdfMetaData.setAuthor("Girl Scouts of the United States of America");
        pdfMetaData.addCreationDate();
        pdfMetaData.setTitle(title);
        Document doc = new Document(pdfDoc, PageSize.A4);
        doc.setBottomMargin(100);
        addHeaderImage(doc, resourceResolver.resolve(path), title);
        addContent(doc, html, request, resourceResolver, new Div());
        doc.close();

        return doc;
    }
    //Add <a link styles
    private String styleLink(String element){
        String linkEl = element.substring(element.indexOf("<a"), element.indexOf(">", element.indexOf("<a"))+1);
        if(linkEl.contains("style=\"")){
            String newtest = linkEl.replace("style=\"", "style=\"color:#00ae58; text-decoration: none; ");
            element = element.replace(linkEl, newtest);
        }else if(linkEl.contains("style='")){
            String newtest = linkEl.replace("style='", "style='color:#00ae58; text-decoration: none; ");
            element = element.replace(linkEl, newtest);
        }else{
            element = element.replaceAll("<a","<a style='color:#00ae58; text-decoration: none;' ");
        }
        return element;
    }
    //Generate asset and create dataURI for pdf generation
    //Create buffered image from asset and encode byte array to base64 format
    private String generateImage(String element, ResourceResolver rr){
        //String s = element.substring(element.indexOf("src=\"") + 5, element.indexOf("\"", element.indexOf("src=\"") + 5));
        LinkedList<String> sources = new LinkedList<>();
        int index = 0;
        while(element.indexOf("src=\"",index+1) >= 0){
            index = element.indexOf("src=\"",index+1);
            sources.add(element.substring(index + 5, element.indexOf("\"", index + 5)));
        }
        for(String s : sources){
            try{
                String ext = s.substring(s.lastIndexOf(".")+1);
                Asset asset = rr.resolve(URLDecoder.decode(s, "UTF8")).adaptTo(Asset.class);
                Rendition orig = asset.getOriginal();
                InputStream is = orig.getStream();
                BufferedImage imgs = ImageIO.read(is);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String byteString;
                String dataUriEncoded;
                ImageIO.write(imgs, ext.toUpperCase(),out);
                byte[] bytes = out.toByteArray();
                dataUriEncoded = java.util.Base64.getEncoder().encodeToString(bytes);
                byteString= "data:image/"+ext+";base64," + dataUriEncoded;
                element = element.replace(s, byteString);
            }catch (Exception e){
                log.debug("Failed to retrieve image asset, cannot find image: "+s);
            }
        }
        return element;

    }
    private String buildHref(String element, String hostname){
        if(!element.contains("href=\"http")){
            element = element.replaceAll("href=\"/", "href=\""+hostname+"/");
        } else{
            element = element.replaceAll("href=\"/content", "href=\""+hostname+"/content");
        }
        return element;
    }
    private void addContent(Document doc, String html, SlingHttpServletRequest request, ResourceResolver rr, Div parent){
        //TODO: TEST ON UAT/DEV ENVIRONMENTS
        //Handle hostname resolution for <a href links in html
        String hostname = request.getRequestURL().toString();
        hostname  = hostname.substring(0,hostname.indexOf("/", hostname.indexOf("//")+2));
        try{
            String[] elements = html.split("~@");
            for(int i = 0; i<elements.length; i++){
                //Fix inline styler issues
                elements[i] = elements[i].replaceAll("<div","<div style='font-family: \"Trefoil Sans Web\", \"Open Sans\", Arial, sans-serif; list-style-position: inside;'");
                elements[i] = elements[i].replaceAll("h6","h4");      //update accordion font size
                if(elements[i].contains("href")){
                    elements[i] = buildHref(elements[i],hostname);
                }
                //Handle <a link styles (Green and no underline)
                if(elements[i].contains("<a")){
                    elements[i] = styleLink(elements[i]);
                }
                //handle images by resolving src to asset, create BufferedImage, get encoded byte array, and create the dataUri string()
                if(elements[i].contains("src=\"")){
                    String substr = elements[i].substring(elements[i].indexOf("<img"), elements[i].indexOf(">", elements[i].indexOf("<img")));
                    if(!substr.contains("http")){
                        //Create buffered image from asset and encode byte array to base64 format
                        elements[i] = generateImage(elements[i],rr);
                    }
                }
                //Add elements to document.
                for(IElement el : HtmlConverter.convertToElements(elements[i])){
                    try{
                        Div div = (Div)el;
                        div.setMarginRight(30);
                        div.setMarginLeft(30);
                        parent.add(div);
                    }catch (Exception e){
                        log.debug("Failed to add element to document", e);
                    }
                }
            }
            doc.add(parent);
        }catch (Exception e){
            log.error("Error adding html to pdf document: ", e);
        }

    }
    //Add council logo image as header
    private void addHeaderImage(Document doc, Resource assetRes, String title){
        if(assetRes!= null) {
            try{
                Asset asset = assetRes.adaptTo(Asset.class);
                Rendition original = asset.getOriginal();
                InputStream is = original.getStream();
                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(IOUtils.toByteArray(is));
                img.setBorderColor(BaseColor.BLACK);
                ImageData data = ImageDataFactory.create(img.getOriginalData());
                Image imgData = new Image(data);
                Div div = new Div();
                com.itextpdf.kernel.colors.Color myColorGreen = new DeviceRgb(0, 128, 0);
                imgData.setHeight(50);
                div.setPadding(10);
                div.setMarginLeft(0);
                doc.setMargins(0,0,0,0);
                div.setBackgroundColor(myColorGreen);
                div.setPaddingRight(0);
                div.add(imgData);
                doc.add(div);
                doc.add(new Paragraph(" "));

            }catch (Exception e){
                log.error("Error retrieving image data: ", e);
            }
        }
    }
    private static byte[] getFontData(String location, ResourceResolver resourceResolver) throws IOException {


        Resource fontResource = resourceResolver.resolve(location);

        byte[] fontData;
        try {
            fontData = IOUtils.toByteArray(fontResource.adaptTo(InputStream.class));
        } catch (IOException e) {
            fontData = new byte[0];
        }
        return Woff2Converter.convert(fontData);

    }

}