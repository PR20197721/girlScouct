
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

String rootPath = "/content";//gscsnj/en/jcr:content/header/global-nav
Resource content = resourceResolver.resolve(rootPath)
Iterator<Resource> sites = content.listChildren();
while (sites.hasNext()) {
    Resource res = sites.next();
    if(res.getChild("en/jcr:content/header/global-nav/links")){
        Resource nav= res.getChild("en/jcr:content/header/global-nav/links");
        //println(res.getChild("en/jcr:content/header/global-nav").getPath());
        Iterator<Resource> navChild = nav.listChildren();
        while(navChild.hasNext()){
            Resource navChildResource = navChild.next();
            String url = navChildResource.getValueMap().get("url");
            //println("URL"+url);
            if(url.equals("https://mygs.girlscouts.org/my-account") || url.equals("https://mygs.girlscouts.org/my-account/")){
                println("COUNCIL"+nav.getPath());
                println("URL LOCATION "+navChildResource.getPath());
                ModifiableValueMap dataNode = navChildResource.adaptTo(ModifiableValueMap.class);
                dataNode.put("openInNewTab", "_blank");
                nav.getResourceResolver().commit();
            }
        }
    }

}