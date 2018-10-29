<%@page session="false" import="
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.List,
                  java.util.Comparator,
                  java.util.Iterator,
                  java.util.ArrayList,
                  java.util.HashMap,
                  java.util.Locale,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.EmptyDataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.day.cq.wcm.api.Page,
                  com.day.cq.wcm.api.PageManager"%>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects /><%
request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
Resource datasource = resource.getChild("datasource");
ValueMap dsProperties = ResourceUtil.getValueMap(datasource);
String type = dsProperties.get("type", String.class);
String sortby = dsProperties.get("sortby", String.class);
if (type != null) {
	ResourceResolver resolver = slingRequest.getResourceResolver();	
	Resource datasourceItems = resolver.resolve(component.getPath()+"/"+type);
	Iterator<Resource> items = datasourceItems.listChildren();
	if(items !=null && items.hasNext()){
		//Create an ArrayList to hold data
		List<Resource> itemsList = new ArrayList<Resource>();
		ValueMap vm = null; 
		while(items.hasNext()){
			Resource item = items.next();
			try{
				ValueMap itemProps = item.getValueMap();
				//allocate memory to the Map instance
				vm = new ValueMapDecorator(new HashMap<String, Object>());   
				//populate the map
				String value = "";
				String text = "";
				try{
					value = String.valueOf(itemProps.get("value"));
					text = itemProps.get("text") != null ? String.valueOf(itemProps.get("text")):String.valueOf(itemProps.get("jcr:title"));					
				}catch(Exception e1){
					
				}
				vm.put("value",value);
				vm.put("text",text);
				itemsList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
			}catch(Exception e){  
				
			} 	
		}	
		if(sortby != null){
			itemsList.sort(new ResourceByPropertyComparator(sortby)); 
		}
		//Create a DataSource that is used to populate the drop-down control
		DataSource ds = new SimpleDataSource(itemsList.iterator());
		request.setAttribute(DataSource.class.getName(), ds);
	}	
}
%>
<%!
class ResourceByPropertyComparator implements Comparator<Resource>{
	
	private final String sortBy;
	
	public ResourceByPropertyComparator(String sortBy){
		this.sortBy=sortBy;
	}
	
	public int compare(Resource r1, Resource r2) {
		ValueMap r1Properties = ResourceUtil.getValueMap(r1);
		ValueMap r2Properties = ResourceUtil.getValueMap(r2);
		int res = 0;
		try{
			String s1 = String.valueOf(r1Properties.get(sortBy));
			String s2 = String.valueOf(r2Properties.get(sortBy));
			res = s1.compareTo(s2);
		}catch(Exception e){
			
		}
        return res;
    }
}
%>