
<%!
  public String getPath(Node reNode){
	String path="";
   try{	
    	path= reNode.getPath()+".html";
   }catch(Exception e){
	   path= "";
   }
	return path;
}


 public String getTitle(Node nNode){
	try{
	 	return(nNode.hasProperty("jcr:title") ? nNode.getProperty("jcr:title").getString() : ""); 
    }catch(Exception e){
    	return "";
    }
	
 }
 
 public String getDate(Node nNode){
	 //DateFormat inFormatter = new SimpleDateFormat("MM/dd/yy");
     //Format formatter = new SimpleDateFormat("dd MMM yyyy");
     DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
 
	 String newsDateStr ="";
 	 try{
	 	if (nNode.hasProperty("date")) {
			Date dateString = nNode.getProperty("date").getDate().getTime();
			newsDateStr = dateFormat.format(dateString);
			//newsDateStr = formatter.format(newsDate);
		}
 	 }catch(Exception e){return "";}	
    return newsDateStr;
 }
 
 public String getDesc(Node nNode){
    try{	 
 		 return(nNode.hasProperty("description") ? nNode.getProperty("description").getString() : "");
    }catch(Exception e){return "";}
 }
 
 public String getImgPath(Node nNode){
	try{ 
  		return nNode.getPath()+"/middle/par/text/image";
	}catch(Exception e){ return "";}
 }
 
 
 public String getText(Node nNode){
	try{
		return(nNode.hasProperty("middle/par/text/text")? nNode.getProperty("middle/par/text/text").getString():"");
	}catch(Exception e){return "";} 
	 
 }
 
 public String getExternalUrl(Node nNode){
	 try{
	 	return(nNode.hasProperty("external-url")?nNode.getProperty("external-url").getString():"");
	 }catch(Exception e){return "";}
 }
 
 public String getDate_yyyyMMdd(Node nNode){
       DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
 
     String newsDateStr ="";
     try{
        if (nNode.hasProperty("date")) {
            Date dateString = nNode.getProperty("date").getDate().getTime();
            newsDateStr = dateFormat.format(dateString);            
        }
     }catch(Exception e){return "";}    
    return newsDateStr;
 }
%>