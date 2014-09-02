<%@page import="org.girlscouts.vtk.utils.imports.ImportGSDocs"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<html>
<body>
<script>


function importFile(mid){
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.admin.importMeeting.html?mid="+mid,
		cache: false
	}).done(function( html ) {
		//location.reload();
		alert("Import file: "+ mid);
	});
}
</script>


<style>
	li{  font-weight:normal; padding-left:40px;}

</style>

<%@include file="../admin/toolbar.jsp"%>
<h1>Preview Import Files</h1>

<%
String fileDir= "/tmp/import/assets";
String fileId=  new java.util.Date().getDate() + "_" + Math.random();
String fileLoc= fileDir +"/"+ request.getParameter("id")+"/jcr:content";
//out.println(fileLoc);
%>

<div style="background-color:yellow;">

         <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  enctype="multipart/form-data">
               <input type="hidden" name="loc" value="<%=fileDir%>"/>
               <input type="hidden" name="id" value="<%=fileId%>"/> 
               <input type="file" id="custasset" name="custasset" size="50" />
               <br />
               <input type="submit" value="Upload File" />
         </form>
         
         
</div>

<%
//out.println("--"+request.getParameter("id"));
if( request.getParameter("id")==null){return;}









/*


try {
	java.io.InputStream fs = new java.net.URL("http://localhost:4503"+ fileLoc).openConnection().getInputStream();
	org.apache.poi.xwpf.usermodel.XWPFDocument hdoc = new org.apache.poi.xwpf.usermodel.XWPFDocument(org.apache.poi.openxml4j.opc.OPCPackage.open(fs));

    java.util.List<org.apache.poi.xwpf.usermodel.XWPFParagraph> parags = hdoc.getParagraphs();
    for (int p = 0; p < parags.size(); p++) {
    	org.apache.poi.xwpf.usermodel.XWPFParagraph Par = parags.get(p);
        
    	
    	out.println("**** "+Par.getStyleID() +" : "+
    	Par.getStyle()+" : "+
        Par.getSpacingAfterLines()+" : "+
        Par.getSpacingBefore()+" : "+
        Par.getSpacingBeforeLines() +" : "+
        
        Par.getIndentationRight()+" : "+
        Par.getIndentationHanging()+" :: "+
        Par.getIndentationLeft()+" : "+
		Par.getIndentationFirstLine()  
    			);
    	
    	if (Par.getNumID()!= null || 
    			(Par.getStyleID() !=null && Par.getStyleID().equals("C-Toolkitcopywbullets")))
    		out.println( "<hr/><li>"+Par.getParagraphText()+"</li>");
       		//out.println( "<hr/>"+ Par.getDocument().getBodyElements().get(0) );
    	else
    		out.println( "<hr/>"+Par.getParagraphText());
    		
    		
        java.util.List<org.apache.poi.xwpf.usermodel.XWPFRun> runs = Par.getRuns();
                for (int y = 0; y < runs.size(); y++) {
                	org.apache.poi.xwpf.usermodel.XWPFRun run = runs.get(y);
                    String str = run.toString().toString();
                    //out.println("<br/>** "+str);
                }
    }

   
}catch(Exception e){e.printStackTrace();}
if(true)return;

*/


final org.apache.sling.jcr.api.SlingRepository repos = sling.getService(org.apache.sling.jcr.api.SlingRepository.class);
javax.jcr.Session _session = repos.loginAdministrative(null);
ImportGSDocs importer = new ImportGSDocs(_session);

/*
org.girlscouts.vtk.models.Meeting meeting = importer.getMeetings(fileDir +"/"+ request.getParameter("id")+"/jcr:content");// fileDir+"/"+ fileId);
if( meeting==null ){

	out.println("No meetings processed");
	return;
}

java.util.Map <String, org.girlscouts.vtk.models.JcrCollectionHoldString>infos  = meeting.getMeetingInfo();
*/


org.girlscouts.vtk.models.Meeting meeting =importer.doSingleFile( fileLoc );
//java.util.Map <String, String>infos  = importer.getMeetings(fileLoc);//fileDir +"/"+ request.getParameter("id")+"/jcr:content");// fileDir+"/"+ fileId);
java.util.Map <String, org.girlscouts.vtk.models.JcrCollectionHoldString>infos  = meeting.getMeetingInfo();

String fileToRm= fileDir +"/"+ request.getParameter("id");
if( fileToRm!=null && !fileToRm.equals("") && fileToRm.startsWith("/tmp/")){
System.err.println("cleaning file... "+ fileToRm );

	//java io
	//-importer.doClean(fileToRm);
	
	//jcr
	//-doIt();
}

String mid=infos.get("meeting id").getStr();
mid = mid.replaceAll("\\[\\[_(.*?)\\]\\]" ,"");
mid = mid.replace("<b>","").replace("</b>", "").replace("<p>","").replace("</p>","").replace("<li>","").replace("</li>","");
%>
<div style="background-color:lightgray;">

<!--  <a href="/content/girlscouts-vtk/en/vtk.admin.importMeeting.html?mid=<%=mid%>">Import file (<%=mid%>)</a> -->

<a href="javascript:void(0)" onclick="importFile('<%=mid%>')">Import file (<%=mid%>)</a>

         
</div>

<%

java.util.Iterator itr = infos.keySet().iterator();
int bgColor=9;
while( itr.hasNext()){
	
			String name= (String)itr.next();
			String txt = infos.get(name).getStr();

			txt = txt.replaceAll("\\[\\[_(.*?)\\]\\]" ,"");
	
			java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\[\\[Activity(.*?)\\]\\]");
			java.util.regex.Matcher m = p.matcher(txt);
		     while(m.find())
		      {
		        //out.println("**"+m.group()+"**");
		        String rpls="Activity ";
		        java.util.StringTokenizer t= new java.util.StringTokenizer(  m.group() ,"|");
		        t.nextToken();
		        rpls += t.nextToken() +" : ";
		        rpls += t.nextToken().replace("]]", "");
		        txt = txt.replace( m.group(), rpls );
		      }
		     
		     
			
	
	%>
		<div style="border:1px solid #000; background-color:#<%=bgColor%>54713">
			<h2><%=name %></h2>
			<%=txt%>
		</div>
	<%
	bgColor--;
}

%>
</body>
</html>


<%!


public void doIt(){
	
	try{
		
		
		
		javax.jcr.Repository repository = org.apache.jackrabbit.commons.JcrUtils.getRepository("http://localhost:4503/crx/server/");
		 /*
	        
	        //Workspace Login
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        Session session = null;
	        session = repository.login(creds, "crx.default");
		
		
	javax.jcr.Repository repository = org.apache.jackrabbit.commons.JcrUtils.getRepository("http://localhost:4503/crx/server/");

    // Workspace Login
    javax.jcr.SimpleCredentials creds = new javax.jcr.SimpleCredentials("admin",
            "admin".toCharArray());
    
    javax.jcr.Session session = null;
    session = repository.login(creds, "crx.default");
    javax.jcr.Node root = session.getRootNode();
 
	Node fileNode = root.getNode("/tmp/import/assets/15_0.03358374794012875/");
			Node jcrContent = fileNode.getNode("jcr:content");
			String fileName = fileNode.getName();
			InputStream content = jcrContent.getProperty("jcr:data").getStream();
			System.err.println("CHK: "+ (content ==null ));
		*/	
	}catch(Exception e){e.printStackTrace();}
}
%>