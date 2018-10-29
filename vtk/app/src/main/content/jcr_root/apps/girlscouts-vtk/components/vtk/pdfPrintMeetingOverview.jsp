<%@page import="java.io.DataOutputStream,
		    com.itextpdf.text.DocumentException,
		    java.io.DataOutput,com.itextpdf.text.pdf.PdfPTable,
		    com.itextpdf.text.pdf.PdfWriter,
		    java.io.ByteArrayOutputStream,
		    com.itextpdf.text.Document,
			org.girlscouts.vtk.ejb.SessionFactory,
			javax.jcr.Session,
			javax.jcr.Node
		    "%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
	response.setContentType("application/pdf");
	Document document = new Document(); 
	StringBuilder output = new StringBuilder();
	String meeting_overview="", meeting_name="";

	String meetingPath = request.getParameter("meetingPath");
	if (meetingPath == null){
		output.append( "Meeting overview not found" );
	}else{

        final SessionFactory sessionFactory = sling.getService(SessionFactory.class);
        Session jcr_session = null;
        ResourceResolver rr= null;
        Node meetingNode = null, meetingInfoNode=null;
	    try{
          
	    	rr = sessionFactory.getResourceResolver();
	    	jcr_session = rr.adaptTo(Session.class);
	    	
            meetingNode =  jcr_session.getNode( meetingPath );
            meeting_name = meetingNode.getProperty("name").getString();
    
            meetingInfoNode =  jcr_session.getNode( meetingPath + "/meetingInfo/overview" );
            meeting_overview = meetingInfoNode.getProperty("str").getString();

        } catch (Exception e) {
            output.append( "Could not retrieve meeting overview." );
			e.printStackTrace();
		} finally {
			try {
				if( rr!=null )
					sessionFactory.closeResourceResolver( rr );
				if (jcr_session != null)
					jcr_session.logout();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	if( ! "".equals( meeting_name ) ){

         output.append( "<h2>" + meeting_name + ": introduction</h2>" );
         output.append( meeting_overview );
    }


    try {
    
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            com.itextpdf.text.html.simpleparser.HTMLWorker htmlWorker = new com.itextpdf.text.html.simpleparser.HTMLWorker(
                    document);
            htmlWorker.parse(new java.io.StringReader( output.toString() ));
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

%>