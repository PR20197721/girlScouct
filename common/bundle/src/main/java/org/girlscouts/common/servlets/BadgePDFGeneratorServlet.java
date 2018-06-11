package org.girlscouts.common.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.pdf.BadgeGenerator;

import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("serial")
@SlingServlet(
		resourceTypes = {"girlscouts-common/servlet/badge-pdf"},
		methods="POST"
)

public class BadgePDFGeneratorServlet extends SlingAllMethodsServlet {

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		process(request, response);
	}

	public static ThreadLocal<ResourceResolver> resolverLocal = new ThreadLocal<>();
	private void process(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		try {
			response.setContentType("application/pdf");
			resolverLocal.set(request.getResourceResolver());
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			new BadgeGenerator(request.getParameter("html"), bais).generatePdf();
			response.setContentLength(bais.size());
			bais.writeTo(response.getOutputStream());
			bais.flush();
			response.flushBuffer();
		}finally {
			resolverLocal.remove();
		}
	}

}
