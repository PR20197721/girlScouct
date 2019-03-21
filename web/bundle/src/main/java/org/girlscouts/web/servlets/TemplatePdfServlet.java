package org.girlscouts.web.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

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
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        //Verify user email request
        Resource r = request.getResource();
        ResourceResolver resourceResolver = request.getResourceResolver();
        response.getOutputStream();
        PdfWriter writer  = null;
        Document document = new Document(PageSize.A4, 20f, 20f, 20f, 50f);
        try{
            log.debug("Getting instance of PdfWriter");
            writer = PdfWriter.getInstance(document, response.getOutputStream());
            String[] footer = {"test"};
            Page homepage = request.getResource().adaptTo(Page.class).getAbsoluteParent(2);
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
        log.error("TemplatePdfError GET");

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
    public class HeaderFooter extends PdfPageEventHelper {
        int pagenumber=0;
        String footerTxtLine1="", footerTxtLine2, path;
        ResourceResolver rr;
        Image header = null;
        public HeaderFooter(String footer[], String _path, ResourceResolver _rr){
            footerTxtLine1= footer[0];
            footerTxtLine2= footer[1];
            path= _path;
            rr = _rr;
            header = loadHeaderImage(_path);
        }

        private Image loadHeaderImage(String imagePath) {
            log.debug("Loading image from crx at {}",imagePath);
            try{
                Resource assetRes = rr.resolve(imagePath);
                if(assetRes!= null) {
                    Asset asset = assetRes.adaptTo(Asset.class);
                    Rendition original = asset.getOriginal();
                    InputStream is = original.getStream();
                    Image img = Image.getInstance(IOUtils.toByteArray(is));
                    img.setBorder(Image.NO_BORDER);
                    return img;
                }
            }catch(Exception e2) {
                log.error("Exception thrown loading {}",imagePath, e2);
            }
            return null;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
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
        public void onEndPage(PdfWriter writer, Document document) {
        }
    }

}