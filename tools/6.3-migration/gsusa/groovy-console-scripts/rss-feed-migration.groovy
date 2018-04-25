	import javax.jcr.Node;
	import javax.jcr.Value;
	import javax.jcr.Property;
	import javax.jcr.PropertyType;
	import org.apache.jackrabbit.commons.JcrUtils;
	import javax.jcr.query.*;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Map;
	import java.util.HashMap;
	import java.util.Arrays;
	import org.girlscouts.web.gsusa.components.rssfeed.RssFeedPathItem;
	import org.apache.commons.lang3.StringUtils;
	
	
	String QUERY_LANGUAGE = "JCR-SQL2";
	String PATH = "/content";
	String RESOURCE_TYPE = 'gsusa/components/rss-feed';
	
	String EXPRESSION = "SELECT s.[jcr:path] "+
	                    "FROM [nt:unstructured] AS s "+
	                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
	                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
	                    
	QueryResult result = search(EXPRESSION, QUERY_LANGUAGE);
	
	if (result != null) {
		try {
			RowIterator rowIter = result.getRows()
			while (rowIter.hasNext()) {
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				
				// Script has already run - don't mess with it.
				if(!node.hasProperty("feedPaths")){
					return;
				}
				
				if(node.hasNode("feedItems")){
					// Remove whatever was there before.
				    node.getNode("feedItems").remove();
				}
				
				// Create the new node.
		        Node feedItemsNode = node.addNode("feedItems", "nt:unstructured");
		        
		        // Empty lists to make things easier.
				List<RssFeedPathItem> entries = new ArrayList<>();
				List<Value> values = new ArrayList<>();
				
				// Get the values list.
				Property feedPathsListProp = node.getProperty("feedPaths");
				if(feedPathsListProp.isMultiple()){
				    values = Arrays.asList(feedPathsListProp.getValues());
				}else{
				    values.add(feedPathsListProp.getValue());
				}
				
				// Split values out of strings.
				for(Value v : values){
					RssFeedPathItem item = RssFeedPathItem.fromLegacyString(v.getString());
					if(item != null){
						entries.add(item);
			        }
				}

				// Create new child nodes from entries.
				println("processing: " + entries.size() + " entries");
				for(int i = 0; i < entries.size(); i++){
                		Node itemNode = feedItemsNode.addNode("item" + i, "nt:unstructured");
                		RssFeedPathItem entry = entries.get(i);
                		
                		if(!StringUtils.isBlank(entry.getPath())){
                		    itemNode.setProperty("path", entry.getPath());
                		}
                		
            		    itemNode.setProperty("page", entry.isPage());
            		    itemNode.setProperty("subDir", entry.isSubDir());
               	}

				// Uncomment before checking in.
		        // node.getProperty("feedPaths").remove();

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
