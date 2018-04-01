package org.girlscouts.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface GSPOST {
		public void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            		throws ServletException, IOException;
}
