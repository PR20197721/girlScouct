package org.girlscouts.vtk.impl.servlets;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
//import javax.mail.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.rmi.ServerException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;

import org.apache.sling.api.resource.ResourceResolverFactory;

import com.day.cq.commons.jcr.JcrUtil;

/*
 @SlingServlet(
 resourceTypes = "sling/servlet/default",
 selectors = "hello",
 extensions = "js",
 methods = "GET"
 )
 */

@Component(label = "vtk upload assets", description = "vtk upload assets", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "asset"),
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = {
				"POST", "GET" }) })
public class Asset extends SlingAllMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {

		doPost(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {

		ResourceResolver resourceResolver = null;
		try {
			final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload
					.isMultipartContent(request);

			PrintWriter out = null;

			out = response.getWriter();

			if (isMultipart) {

				final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();

				for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {

					final String k = pairs.getKey();
					final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
					final org.apache.sling.api.request.RequestParameter param = pArr[0];
					final InputStream stream = param.getInputStream();
					if (param.isFormField()) {

						String t = org.apache.commons.fileupload.util.Streams.asString(stream);

						if (k.equals("custasset")) {

							byte[] db64 = Base64.decodeBase64(t);

							InputStream inn = new ByteArrayInputStream(db64);

							resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
							Session session = resourceResolver.adaptTo(Session.class);
							reverseReplicateBinary(session,
									request.getParameter("loc"),
									request.getParameter("id"), inn,
									request.getParameter("assetDesc"),
									request.getParameter("owner"),
									request.getParameter("id"));

						}

					} else {

						resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
						Session session = resourceResolver.adaptTo(Session.class);

						String loc = request.getParameter("loc");
						String name = request.getParameter("id");
						if (request.getParameter("newvalue") != null) {
							loc = "/content/dam/girlscouts-vtk/local/icon/meetings";
							name = name + ".png";
							if (request.getParameter("newvalue") != null) {
								// out.println("<img src=\""+ loc +"/"+ name
								// +"\"/>");
								// out.println("<script>location.reload();</script>");
							}
						} else if (request.getParameter("upldTroopPic") != null) {
							loc = "/content/dam/girlscouts-vtk/troop-data/"+ request.getParameter("councilId") +"/" + request.getParameter("troopId") + "/imgLib";
							name = "troop_pic.png";
						}

						reverseReplicateBinary(session, loc, name, stream,
								request.getParameter("assetDesc"),
								request.getParameter("owner"),
								request.getParameter("id"));

					}

				}
			} else {

				final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();

				for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {

					final String k = pairs.getKey();
					final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();

					final org.apache.sling.api.request.RequestParameter param = pArr[0];
					final InputStream stream = param.getInputStream();
					if (param.isFormField()) {
						;
					} else {

						resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
						Session session = resourceResolver.adaptTo(Session.class);
						reverseReplicateBinary(session,
								request.getParameter("loc"),
								request.getParameter("id"), stream,
								request.getParameter("assetDesc"),
								request.getParameter("owner"),
								request.getParameter("id"));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (request.getParameter("loc") != null && request.getParameter("loc").contains("/tmp/import/assets")) {
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.previewImportMeeting.html?id=" + request.getParameter("id"));
		} else if (request.getParameter("upldTroopPic") != null) {
			try {
				// wait 1 seconds for image to upload
				Thread.sleep(1000); 
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			// clear image cache
                        response.addHeader("Cache-Control", "no-cache,must-revalidate");
                        response.addHeader("Expires", "Mon, 26 Jul 2014 05:00:00 GMT");
                        response.addHeader("Pragma", "no-cache");
			response.addHeader("newTroopPhoto", "true");
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.myTroop.html?newTroopPhoto=true");
		}
	}

	private void reverseReplicateBinary(Session session, String parentPath, String name, InputStream is, String desc, String owner, String id) throws RepositoryException {
		ValueFactory valueFactory = session.getValueFactory();
		Node page = JcrUtil.createPath(parentPath, "nt:unstructured", "nt:unstructured", session, true);
		Node file = null;
		if (page.hasNode(name)) {
			file = page.getNode(name);
		} else {

			try {
				file = page.addNode(name, "nt:file");

			} catch (javax.jcr.ItemExistsException ex) {
				file = page.getNode(name);
				ex.printStackTrace();
			}
		}

		Node resource = null;
		if (file.hasNode("jcr:content")) {
			resource = file.getNode("jcr:content");
			resource.remove();
		}
		resource = file.addNode("jcr:content", "nt:resource");
		resource.setProperty("jcr:data", valueFactory.createBinary(is));
		session.save();
	}
}
