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
	import org.girlscouts.web.gsusa.component.rightrailcarousel.RightRailCarouselItem;
	import org.apache.commons.lang3.StringUtils;
	
	
	String QUERY_LANGUAGE = "JCR-SQL2";
	String PATH = "/content";
	String RESOURCE_TYPE = 'gsusa/components/right-rail-carousel';
	
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
				if(!node.hasProperty("carouselList")){
					return;
				}
				
				if(node.hasNode("carouselItems")){
					// Remove whatever was there before.
				    node.getNode("carouselItems").remove();
				}
				
				// Create the new node.
		        Node carasouelItemsNode = node.addNode("carouselItems", "nt:unstructured");
		        
		        // Empty lists to make things easier.
				List<RightRailCarouselItem> entries = new ArrayList<>();
				List<Value> values = new ArrayList<>();
				
				// Get the values list.
				Property carouselListProp = node.getProperty("carouselList");
				if(carouselListProp.isMultiple()){
				    values = Arrays.asList(carouselListProp.getValues());
				}else{
				    values.add(carouselListProp.getValue());
				}
				
				// Split values out of strings.
				for(Value v : values){
					RightRailCarouselItem item = RightRailCarouselItem.fromLegacyString(v.getString());
					if(item != null){
						entries.add(item);
			        }
				}

				// Create new child nodes from entries.
				println("processing: " + entries.size() + " entries");
				for(int i = 0; i < entries.size(); i++){
                		Node itemNode = carasouelItemsNode.addNode("item" + i, "nt:unstructured");
                		RightRailCarouselItem entry = entries.get(i);
                		
                		if(!StringUtils.isBlank(entry.getLink())){
                		    itemNode.setProperty("link", entry.getLink());
                		}
                		if(!StringUtils.isBlank(entry.getLabel())){
                		    itemNode.setProperty("label", entry.getLabel());
                		}
                		if(!StringUtils.isBlank(entry.getImagePath())){
                		    itemNode.setProperty("imagePath", entry.getImagePath());
                		}
                		itemNode.setProperty("newWindow", entry.isNewWindow());
               	}
               	
               	// Some of the boolean attributes are in text form for some reason:
               	if(node.hasProperty("autoscroll")){
               	    Property prop = node.getProperty("autoscroll");
               	    if(prop.getType().equals(PropertyType.STRING)){
               	    		Boolean boolValue = Boolean.valueOf(prop.getString());
	                		prop.remove();
	                		node.setProperty("autoscroll", boolValue);
	                		println("Converted autoscroll");
	            		}
               	}
               	if(node.hasProperty("dynamiccarousel")){
               	    Property prop = node.getProperty("dynamiccarousel");
               	    if(prop.getType().equals(PropertyType.STRING)){
               	    		Boolean boolValue = Boolean.valueOf(prop.getString());
	                		prop.remove();
	                		node.setProperty("dynamiccarousel", boolValue);
	                		println("Converted dynamiccarousel");
	            		}
               	}
               	if(node.hasProperty("showverticalrule")){
               	    Property prop = node.getProperty("showverticalrule");
               	    if(prop.getType().equals(PropertyType.STRING)){
               	    		Boolean boolValue = Boolean.valueOf(prop.getString());
	                		prop.remove();
	                		node.setProperty("showverticalrule", boolValue);
	                		println("Converted showverticalrule");
	            		}
               	}

				// Uncomment before checking in.
		        node.getProperty("carouselList").remove();

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
