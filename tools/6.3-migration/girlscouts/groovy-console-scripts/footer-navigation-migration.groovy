import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'girlscouts/components/footer-navigation';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:unstructured] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			try{
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				if(node.hasProperty("links") ){
				    Node linksNode = null;
				    if(!node.hasNode("footerLinks")){
				        linksNode = node.addNode("footerLinks", "nt:unstructured");
				    
				     println("**************************************************************************************")
				     println(linksNode.getPath())
				     Property linksProperty = node.getProperty("links");
	                 Value[] values = null;
	                 if(!linksProperty.isMultiple()){
	                     values = new Value[1]
	                     values[0] = linksProperty.getValue()
	                 } else{
	                     values = linksProperty.getValues()
	                 }
				     
	                 println("Processing footer links with " + values.length + " values")
	                    
	                 for(int i = 0; i < values.length; i++){
						 try{
		                    String linkSet = values[i].getString()
		                    println(linkSet);
		                    String[] linkProperties = linkSet.split("\\|\\|\\|");
		                    Node itemNode = null
		                    if(!linksNode.hasNode("item" + i)){
		                        itemNode = linksNode.addNode("item" + i, "nt:unstructured")
		                    } else{
		                        itemNode = linksNode.getNode("item" + i);
		                    }
		                    String label = linkProperties[0]
		                    String path = linkProperties.length >= 2 ? linkProperties[1] : ""
		                    String clazz = linkProperties.length >= 3 ? linkProperties[2] : ""
		            
		                    String openInNew = linkProperties.length >= 4 ? linkProperties[3] : ""
		                    
		                    itemNode.setProperty("linkTitle", label)
		                    itemNode.setProperty("url", path)
		                    if(!clazz.isEmpty()){
		                        itemNode.setProperty("class", clazz)
		                    }
		                    if(!openInNew.isEmpty() && (openInNew.equals("true") || openInNew.equals("false")))
		                    {
		                        println(">>>>>>>>>>>set new window")
		                       itemNode.setProperty("newWindow", Boolean.parseBoolean(openInNew))  
		                    }
						 } catch (Exception e2) {
							 println(e2.getMessage())
							 e2.printStackTrace()
						 }
	                        
	                        
	                }
				        
				    save()
				        println("saved converted content")
	                }
				    
				}
	            if(node.hasProperty("socialIcons") ){
				    Node linksNode = null;
				    if(!node.hasNode("socialMediaLinks")){
				        linksNode = node.addNode("socialMediaLinks", "nt:unstructured");
				    
				     println("**************************************************************************************")
				     println(linksNode.getPath())
				    
				     Property socialProperty = node.getProperty("socialIcons");
	                 Value[] values = null;
	                 if(!socialProperty.isMultiple()){
	                     values = new Value[1]
	                     values[0] = socialProperty.getValue()
	                 } else{
	                     values = socialProperty.getValues()
	                 }
				     
	                 println("Processing social links with " + values.length + " values")
	                    
	                 for(int i = 0; i < values.length; i++){
	                    String linkSet = values[i].getString()
	                    println(linkSet);
	                    String[] linkProperties = linkSet.split("\\|\\|\\|");
	                    Node itemNode = null
	                    if(!linksNode.hasNode("item" + i)){
	                        itemNode = linksNode.addNode("item" + i, "nt:unstructured")
	                    } else{
	                        itemNode = linksNode.getNode("item" + i);
	                    }
	                    String url = linkProperties[0]
	                    String icon = linkProperties.length >= 2 ? linkProperties[1] : ""
	            
	                    String openInNew = linkProperties.length >= 3 ? linkProperties[2] : ""
	                    
	                    itemNode.setProperty("url", url)
	                    itemNode.setProperty("icon", icon)
	                    if(!openInNew.isEmpty() && (openInNew.equals("true") || openInNew.equals("false")))
	                    {
	                       itemNode.setProperty("newWindow", Boolean.parseBoolean(openInNew))  
	                    }
	                        
	                        
	                }
				        
				    save()
				        println("saved converted content")
	                }
				    
				}
			} catch (Exception e1) {
			    println(e1.getMessage())
				e1.printStackTrace()
			}
			
		}
	} catch (Exception e) {
	    println(e.getMessage())
		e.printStackTrace()
	}
}

def QueryResult search(EXPRESSION, QUERY_LANGUAGE) {
    println(EXPRESSION)
	QueryResult result = null;
	try {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		Query sql2Query = queryManager.createQuery(EXPRESSION, QUERY_LANGUAGE);
		return sql2Query.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}