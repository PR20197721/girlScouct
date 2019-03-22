package org.girlscouts.web.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorkerFactory;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.WriterProperties;
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


        //Verify user email request
       /* Resource r = request.getResource();
        ResourceResolver resourceResolver = request.getResourceResolver();
        response.getOutputStream();
        PdfWriter writer  = null;
        Document document = new Document(PageSize.A4, 20f, 20f, 20f, 50f);
        try{
            log.debug("Getting instance of PdfWriter");
            writer = PdfWriter.getInstance(document, response.getOutputStream());
            String[] footer = {"test"};
            Resource rsrc = resourceResolver.resolve(request.getResource().getPath().replaceAll("/jcr:content",""));
            Page homepage = rsrc.adaptTo(Page.class).getAbsoluteParent(2);
            Node home = homepage.getContentResource().adaptTo(Node.class);
            String path = "";
            try{
                if(home.hasNode("header/logo/regular")){
                    path = home.getNode("header/logo/regular").getProperty("fileReference").getString();
                }
            }catch (Exception e){
                path = "/content/dam/girlscouts-gsusa/images/logo/logo.png.transform/cq5dam.web.1280.1280/img.png";
            }
            HeaderFooter event=  new HeaderFooter(footer, path, resourceResolver);
            writer.setPageEvent(event);
        }catch(Exception e){
            log.error("Exception occured: ", e);
        }
        log.error(r.getPath());
        log.error("TemplatePdfError GET");*/

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

        sb.append("<p>test</p>");
        sb.append("<br/>");
        sb.append("<p>This is a test</p>");
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
        Document doc = HtmlConverter.convertToDocument(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)) , pdfDoc, props);
        addImage(doc, resourceResolver, resourceResolver.resolve(path));
        doc.close();

        return doc;
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
                doc.add(imgData);
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
    public class HeaderFooter extends PdfPageEventHelper {
        int pagenumber=0;
        String footerTxtLine1="", footerTxtLine2, gradeLevel;
        ResourceResolver rr;
        com.itextpdf.text.Image header = null;
        public HeaderFooter(String footer[], String _gradeLevel, ResourceResolver _rr){
            footerTxtLine1= footer[0];
            footerTxtLine2= footer[1];
            gradeLevel= _gradeLevel;
            rr = _rr;
            header = loadHeaderImage(_gradeLevel, rr);
        }

        private com.itextpdf.text.Image loadHeaderImage(String _gradeLevel, ResourceResolver rr2) {
            String imagePath = "/content/dam/girlscouts-vtkcontent/Print-PDF/"+ gradeLevel +"/topBanner.jpg";
            log.debug("Loading image from crx at {}",imagePath);
            try{
                Resource assetRes = rr.resolve(imagePath);
                if(assetRes!= null) {
                    Asset asset = assetRes.adaptTo(Asset.class);
                    Rendition original = asset.getOriginal();
                    InputStream is = original.getStream();
                    com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(IOUtils.toByteArray(is));
                    img.setBorder(com.itextpdf.text.Image.NO_BORDER);
                    return img;
                }
            }catch(Exception e2) {
                log.error("Exception thrown loading {}",imagePath, e2);
            }
            return null;
        }

        @Override
        public void onStartPage(com.itextpdf.text.pdf.PdfWriter writer, com.itextpdf.text.Document document) {
            pagenumber++;

            try{
                PdfPTable tabHead = new PdfPTable(1);

                tabHead.setWidthPercentage(100);
                tabHead.setSpacingBefore(0f);
                tabHead.setSpacingAfter(0f);

                tabHead.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                PdfPCell cell;
                cell = new PdfPCell();
                cell.setBorder(PdfPCell.NO_BORDER);
                tabHead.addCell(cell);
                if(header != null) {
                    tabHead.addCell(header);
                }
                document.add( tabHead);
                LineSeparator separator = new LineSeparator();
                Chunk linebreak = new Chunk(separator);
                separator.setLineColor(BaseColor.BLACK);

                Font ffont = new Font();
                ffont.setSize(7);
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_CENTER,  new Phrase(linebreak),
                        (document.left() + document.right()) / 2, document.bottom() - 8, 0);

                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_RIGHT , new Phrase(String.format("page %d", pagenumber), ffont),
                        document.right(), document.bottom() - 38, 0);
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, new Paragraph(footerTxtLine1, ffont),
                        document.left() , document.bottom() - 18, 0);

                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, new Phrase(footerTxtLine2, ffont),
                        document.left() , document.bottom() - 28, 0);
            }catch(Exception e){
                log.error("Exception occured: ", e);
            }

        }

        @Override
        public void onEndPage(com.itextpdf.text.pdf.PdfWriter writer, com.itextpdf.text.Document document) {
        }
    }

}