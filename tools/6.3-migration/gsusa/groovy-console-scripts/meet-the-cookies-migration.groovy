	import javax.jcr.Node;
	import javax.jcr.Value;
	import javax.jcr.Property;
	import org.apache.jackrabbit.commons.JcrUtils;
	import javax.jcr.query.*;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Map;
	import java.util.HashMap;
	import java.util.Arrays;
	
	
	String QUERY_LANGUAGE = "JCR-SQL2";
	String PATH = "/content";
	String RESOURCE_TYPE = 'gsusa/components/meet-the-cookies';
	
	String EXPRESSION = "SELECT s.[jcr:path] "+
	                    "FROM [nt:unstructured] AS s "+
	                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
	                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
	                    
	QueryResult result = search(EXPRESSION, QUERY_LANGUAGE);
	
	enum Keys {
		TITLE, IMAGE, DESCRIPTION
	};
	
	if (result != null) {
		try {
			RowIterator rowIter = result.getRows()
			while (rowIter.hasNext()) {
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				
				// Script has already run - don't mess with it.
				if(!node.hasProperty("cookies")){
					return;
				}
				
				if(node.hasNode("cookies")){
					// Remove whatever was there before.
				    node.getNode("cookies").remove();
				}
				
				// Create the new node.
		        Node cookiesNode = node.addNode("cookies", "nt:unstructured");
		        
		        // Empty lists to make things easier.
				List<Map<Keys, String>> entries = new ArrayList<>();
				List<Value> values = new ArrayList<>();
				
				// Get the values list.
				Property cookiesProp = node.getProperty("cookies");
				if(cookiesProp.isMultiple()){
				    values = Arrays.asList(cookiesProp.getValues());
				}else{
				    values.add(cookiesPropl.getValue());
				}
				
				// Split values out of strings.
				for(Value v : values){
					Map<Keys, String> splitValues = new HashMap<>();
					List<String> splitValueList = Arrays.asList(v.getString().split("\\|\\|\\|"));
					boolean hasValue = false;
					if(splitValueList.size() > 0){
						splitValues.put(Keys.TITLE, splitValueList.get(0));
						hasValue = true;
					}
					if(splitValueList.size() > 1){
						splitValues.put(Keys.IMAGE, splitValueList.get(1));
					}
					if(splitValueList.size() > 2){
						splitValues.put(Keys.DESCRIPTION, splitValueList.get(2));
					}
					
					// No sense in adding blank nodes.
					if(hasValue){
						entries.add(splitValues);
					}
				}

				// Create new child nodes from entries.
				for(int i = 0; i < entries.size(); i++){
                		Node itemNode = cookiesNode.addNode("item" + i, "nt:unstructured");
                		Map<Keys, String> entry = entries.get(i);
                		if(entry.containsKey(Keys.TITLE)){
                		    itemNode.setProperty("title", entry.get(Keys.TITLE));
                		}
                		if(entry.containsKey(Keys.IMAGE)){
                		    itemNode.setProperty("image", entry.get(Keys.IMAGE));
                		}
                		if(entry.containsKey(Keys.DESCRIPTION)){
                		    itemNode.setProperty("description", entry.get(Keys.DESCRIPTION));
                		    
                		    // So the editor picks it up non-formatted.
                		    itemNode.setProperty("textIsRich", true);
                		}
               	}
				
				// Uncomment before checking in.
		        node.getProperty("cookies").remove();

				save();
		        println("saved converted content");
				
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
