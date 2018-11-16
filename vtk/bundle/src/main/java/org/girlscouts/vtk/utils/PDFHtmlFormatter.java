package org.girlscouts.vtk.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class PDFHtmlFormatter {

    public static String format(String src) {
        String dst = src;

        // Replace <br> tags with new lines.
        dst = dst.replaceAll("<br[^>]*>", "\n");

        // Cleanup html
        Document doc = Jsoup.parse(dst);
        dst = doc.body().html();

        // Empty bold
        dst = dst.replaceAll("<b>(\\s*?)</b>", "$1");
        dst = dst.replaceAll("</b>(\\s*?)<b>", "$1");
        // Empty italics 
        dst = dst.replaceAll("<i>(\\s*?)</i>", "$1");
        dst = dst.replaceAll("</i>(\\s*?)<i>", "$1");
        // Empty paragraphs
        dst = dst.replaceAll("<p>\\s*?</p>", "");
        // Extra <p> around <li>
        dst = dst.replaceAll("<p><li>(.*?)</li></p>", "<li>$1</li>");
        // Strange apostrophe
        dst = dst.replaceAll("’", "'");
        // Stange double quote
        dst = dst.replaceAll("“", "");
        dst = dst.replaceAll("”", "");
        
        // Cleanup html
        doc = Jsoup.parse(dst);
        dst = doc.body().html();
        dst = Jsoup.clean(dst, Whitelist.relaxed());

        // mdash
        dst = dst.replaceAll("—", "&mdash;");
        return dst;
    }

}
