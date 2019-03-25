package org.girlscouts.web.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorkerFactory;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorkerFactory;
import com.itextpdf.html2pdf.html.AttributeConstants;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.woff2.Woff2Converter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.*;
/*import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;*/
import com.itextpdf.layout.element.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
import org.girlscouts.common.pdf.BadgeGenerator;
import org.girlscouts.common.pdf.GSTagWorkerFactory;
import org.girlscouts.common.servlets.BadgePDFGeneratorServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.itextpdf.html2pdf.HtmlConverter.convertToElements;
import static org.girlscouts.common.pdf.BadgeGenerator.BOLD_FONT_LOCATION;
import static org.girlscouts.common.pdf.BadgeGenerator.FONT_LOCATION;

@SlingServlet(
        label = "Girl Scouts Template Site PDF Servlet", description = "Generate PDF from Template site page", paths = {},
        methods = { "GET", "POST" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "girlscouts/components/page" }, // Ignored if
        // paths is set
        selectors = {"pdfrender"}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class TemplatePdfServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(TemplatePdfServlet.class);
    @Reference
    private transient SlingSettingsService settingsService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        //Verify user email request
        log.error("TemplatePdfError POST");

    }
    public static ThreadLocal<ResourceResolver> resolverLocal = new ThreadLocal<>();
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("application/pdf");
            resolverLocal.set(request.getResourceResolver());
            ByteArrayOutputStream bais = new ByteArrayOutputStream();

            StringBuilder sb = new StringBuilder();
            Resource rsrc = resolverLocal.get().resolve(request.getResource().getPath().replaceAll("/jcr:content",""));
            Page homepage = rsrc.adaptTo(Page.class).getAbsoluteParent(2);
            Node home = homepage.getContentResource().adaptTo(Node.class);
            //Get header path
            String path = "";
            try{
                if(home.hasNode("header/logo/regular")){
                    path = home.getNode("header/logo/regular").getProperty("fileReference").getString();
                }
            }catch (Exception e){
                path = "/content/dam/girlscouts-gsusa/images/logo/logo.png.transform/cq5dam.web.1280.1280/img.png";
            }
            buildHtml(sb, request, resolverLocal.get());
            generatePdf(bais, sb.toString(), path );
            // new BadgeGenerator(request.getParameter("html"), bais).generatePdf();
            response.setContentLength(bais.size());
            bais.writeTo(response.getOutputStream());
            bais.flush();
            response.flushBuffer();
        }finally {
            resolverLocal.remove();
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
    public void buildHtml(StringBuilder sb, SlingHttpServletRequest request, ResourceResolver rr) {

        sb.append("<span>test</span>");
        sb.append("<br/>");
        sb.append("<div>This is a test</div>");
    }

    public Document generatePdf(ByteArrayOutputStream outputStream, String html, String path) throws IOException {

        WriterProperties writerProperties = new WriterProperties();

        PdfWriter pdfWriter = new PdfWriter(outputStream, writerProperties);

        PdfDocument pdfDoc = new PdfDocument(pdfWriter);

        //Set meta tags
        PdfDocumentInfo pdfMetaData = pdfDoc.getDocumentInfo();
        pdfMetaData.setAuthor("Girlscouts America");
        pdfMetaData.addCreationDate();
        pdfMetaData.setKeywords("Girlscouts Template Page");
        pdfMetaData.setSubject("Template Page PDF");
        pdfMetaData.setTitle("Template Page PDF");

        // pdf conversion
        ConverterProperties props = new ConverterProperties();

        // Setup custom tagworker factory for pulling images straight from the DAM.
        ITagWorkerFactory tagWorkerFactory = new GSTagWorkerFactory();

        ResourceResolver resourceResolver = this.resolverLocal.get();

        //props.setTagWorkerFactory(tagWorkerFactory);

        //FontProvider fontFactory = new DefaultFontProvider(true, true, true);
        //fontFactory.addFont(getFontProgram(FONT_LOCATION, resourceResolver));
        //fontFactory.addFont(getFontProgram(BOLD_FONT_LOCATION, resourceResolver));


        FontSet fontSet = new FontSet();
        fontSet.addFont(getFontData(FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web");
        fontSet.addFont(getFontData(BOLD_FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web Bold");


        FontProvider fontFactory = new FontProvider(fontSet);



        props.setImmediateFlush(false);
        props.setFontProvider(fontFactory);
        Document doc = HtmlConverter.convertToDocument(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)) , pdfDoc, props);
        addImage(doc, resourceResolver, resourceResolver.resolve(path));
        addContent(doc, html);
        doc.close();

        return doc;
    }
    public void addContent(Document doc, String html){
        try{
            for(IElement el : HtmlConverter.convertToElements(html)){
                doc.add((Paragraph)el);
            }
        }catch (Exception e){

        }

    }
    public void addImage(Document doc, ResourceResolver rr, Resource assetRes){
        if(assetRes!= null) {
            try{
                Asset asset = assetRes.adaptTo(Asset.class);
                Rendition original = asset.getOriginal();
                InputStream is = original.getStream();
                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(IOUtils.toByteArray(is));
                ImageData data = ImageDataFactory.create(img.getOriginalData());
                Image imgData = new Image(data);
                Div div = new Div();
                Paragraph p2 = new Paragraph("test paragraph2");
                com.itextpdf.kernel.colors.Color myColor = new DeviceRgb(0, 128, 0);
                imgData.setHeight(75);
                div.setPadding(10);
                div.setBackgroundColor(myColor);
                div.add(imgData);
                doc.add(div);
                doc.add(p2);

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