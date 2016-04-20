package org.girlscouts.web.councilrollout;

import java.util.List;

import com.day.cq.wcm.api.Page;
import javax.jcr.Session;

public interface SEOSetter {

    List<Page> recurse(Page p);
    String setSEO(Session s, Page p, String councilName);

}