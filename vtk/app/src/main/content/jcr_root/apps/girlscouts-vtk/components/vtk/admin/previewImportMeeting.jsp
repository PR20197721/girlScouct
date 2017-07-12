<%@page import="org.girlscouts.vtk.utils.imports.ImportGSDocs"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
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
	
	});
}
</script>


<style>
	<!-- li{  font-weight:normal; padding-left:40px;} 
p {
  margin-top: 0em;
  margin-bottom: 0em;
}
-->
</style>

<%@include file="../admin/toolbar.jsp"%>
<h1>Preview Import Files</h1>

<%
String fileDir= "/tmp/import/assets";
String fileId=  new java.util.Date().getDate() + "_" + Math.random();
String fileLoc= fileDir +"/"+ request.getParameter("id")+"/jcr:content";
//out.println(fileLoc);
%>





<div style="background-color:orange;">
<h2>Meeting uploader</h2>
<form action="/content/girlscouts-vtk/en/vtk.admin.previewImportMeeting.html">
<input type="hidden" name="runAll" value="true"/>

	<div class="row">
  <div class="small-24 medium-12 large-12 columns">
  	____ directory 
  </div>
  <div class="small-24 medium-12 large-12 columns">
  	<input type="text" name="mainDir" value="/Users/akobovich/Documents/VTK_Imports/VTK-ASSETS/" style="width:400px;"/>
  </div>
  </div>
  
  <div class="row">
  <div class="small-24 medium-12 large-12 columns">
  ________ XLS file
  </div>
  
  <div class="small-24 medium-12 large-12 columns">
  <input type="text" name="xlsFileName" value="metadata.xlsx" style="width:400px;"/>
  </div>
  </div>
  
  <div class="row">
  <div class="small-24 medium-12 large-12 columns">
  ________Meeting directory
  </div>
  <div class="small-24  medium-12 large-12 columns">
    <input type="text" name="meetingDir" value="meetings" style="width:400px;"/>
  </div>
  </div>
  
  <div class="row">
  <div class="small-24  medium-12 large-12 columns">
  
 	 &nbsp;
  </div>
  <div class="small-24  medium-12 large-12 columns">
  
 	 <input type="submit" value="Run meeting uploader"/>
  </div>
  </div>



   <!-- 
 <a href="/content/girlscouts-vtk/en/vtk.admin.previewImportMeeting.html?runAll=true">run full meeting(s) import</a>
 -->
 </form>
</div>




<div style="background-color:yellow;">
		<h1>Upload Single file:</h1>
         <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  enctype="multipart/form-data">
               <input type="hidden" name="loc" value="<%=fileDir%>"/>
               <input type="hidden" name="id" value="<%=fileId%>"/> 
               <input type="file" id="custasset" name="custasset" size="50" />
               <br/>
               <input type="submit" value="Upload File" />
         </form>
         
        
         
</div>


<%



//out.println("--"+request.getParameter("id"));
if( request.getParameter("id")==null && 
	request.getParameter("runAll")==null){return;}









if( false){

try {
	java.io.InputStream fs = new java.net.URL("http://localhost:4503"+ fileLoc).openConnection().getInputStream();
	org.apache.poi.xwpf.usermodel.XWPFDocument hdoc = new org.apache.poi.xwpf.usermodel.XWPFDocument(org.apache.poi.openxml4j.opc.OPCPackage.open(fs));

    java.util.List<org.apache.poi.xwpf.usermodel.XWPFParagraph> parags = hdoc.getParagraphs();
    for (int p = 0; p < parags.size(); p++) {
    	org.apache.poi.xwpf.usermodel.XWPFParagraph Par = parags.get(p);
        
    	
    	out.println("<br/>**** "+Par.getStyleID() +" : "+
    	Par.getStyle()+" : "+
        Par.getSpacingAfterLines()+" : "+
        Par.getSpacingBefore()+" : "+
        Par.getSpacingBeforeLines() +" : "+
        
        Par.getIndentationRight()+" : "+
        Par.getIndentationHanging()+" :: "+
        Par.getIndentationLeft()+" : "+
		Par.getIndentationFirstLine()  +" : "+
		
		Par.getIndentationFirstLine() + ": "+
		Par.getAlignment().getValue() +": "+ Par.getNumID()
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
                   out.println("<br/>** "+str );//+" : "+run.getTextPosition()+" : "+run.getFontSize() + ": " +run.getFontFamily());
                	//try{out.println(run.getCTR().getRPr().getRStyle().getVal());}catch(Exception e){}
                }
             
    }

   
}catch(Exception e){e.printStackTrace();}
if(true)return;
}



final org.apache.sling.jcr.api.SlingRepository repos = sling.getService(org.apache.sling.jcr.api.SlingRepository.class);
javax.jcr.Session _session = repos.loginAdministrative(null);
ImportGSDocs importer = new ImportGSDocs(_session);

if( request.getParameter("runAll")!=null ){
	
	String mainDir= request.getParameter("mainDir");
	String xlsFileName= request.getParameter("xlsFileName");
	String meetingDir= request.getParameter("meetingDir");
	
	out.println("<br/>Running parsers....");
	ImportGSDocs igd =new ImportGSDocs(_session, mainDir, meetingDir, xlsFileName);
	igd.getMeetings();
	out.println(igd.getActivityLog());
	out.println("<br/><br/>parser completed "+ new java.util.Date());
	
	return;
}



org.girlscouts.vtk.models.Meeting meeting =importer.doSingleFile( fileLoc );
java.util.Map <String, org.girlscouts.vtk.models.JcrCollectionHoldString>infos  = meeting.getMeetingInfo();

String fileToRm= fileDir +"/"+ request.getParameter("id");
if( fileToRm!=null && !fileToRm.equals("") && fileToRm.startsWith("/tmp/")){
}

String mid=infos.get("meeting id").getStr();
mid = mid.replaceAll("\\[\\[_(.*?)\\]\\]" ,"");
mid = mid.replace("<b>","").replace("</b>", "").replace("<p>","").replace("</p>","").replace("<li>","").replace("</li>","");
%>
<div style="background-color:lightgray;">
<a href="javascript:void(0)" onclick="importFile('<%=mid%>')">Import file (<%=mid%>)</a>

         
</div>

<%

java.util.Iterator itr = infos.keySet().iterator();
int bgColor=9;
while( itr.hasNext()){
	
			String name= (String)itr.next();
			String txt = infos.get(name).getStr();
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
	}catch(Exception e){e.printStackTrace();}
}
%>