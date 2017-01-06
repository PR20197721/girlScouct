<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%>
<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="java.util.ArrayList,
                  javax.jcr.Node,
                  java.util.Iterator,
                  java.util.Map,
				  java.util.List,
                  java.util.Arrays,
                  java.util.Map.Entry,                  
                  org.apache.jackrabbit.JcrConstants,
                  org.apache.sling.api.resource.Resource,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.ds.AbstractDataSource,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.day.cq.tagging.Tag,
                  com.day.cq.tagging.TagConstants,
                  com.day.cq.tagging.TagManager"%><%
/**
*   A datasource returning tags and namespaces. An optional <code>filters</code> can
*   be specified which is matched against the node type of
*   the resource if exists. ELs can be specified for limit and offset. All the other
*   properties will be added as request attributes with name-value pairs as in the 
*   properties.
*
*   @datasource
*   @name TagsDatasource
*   @location /libs/cq/tagging/gui/datasources/tagsdatasource
*
*   @property {String[]} filters which is used to filter the resources based on node/mime type
*   @property {String} limit an EL that specifies number of resources to be fetched
*   @property {String} offset an EL that specifies the offset
*   @property {String} &lt;other&gt; will be added as request attribute

*   @example
*   + datasource
*      - jcr:primaryType = "nt:unstructured"
*      - sling:resourceType = "cq/tagging/gui/datasources/tagsdatasource"
*      - limit = "10"
*      - offset = "${empty requestPathInfo.selectors[1] ? &quot;10&quot; : requestPathInfo.selectors[1]}"
*      - filters = "[cq:Tag]"
   
*/
final int ROWS_DEFAULT = 15;
final int ROWS_NEXT_TIME_ONWARDS = 10;
ExpressionHelper ex = cmp.getExpressionHelper();
TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
String suffix = slingRequest.getRequestPathInfo().getSuffix();
Iterator<Tag> it;
if (suffix == null) {
    it = tagMgr.getNamespacesIter();
} else {
    Tag tag = tagMgr.resolve(suffix);
    if (tag == null) {
        return;
    }
    it = tag.listChildren();
}

Config cfg = new Config(resource);
Config dsCfg = new Config(resource.getChild(Config.DATASOURCE));
String filters[] = dsCfg.get("filters", String[].class);

int count = 0;
int rows = ROWS_DEFAULT;
int offset = 0;

//get the count of the total no. of tags

List<Resource> rv = new ArrayList<Resource>(ROWS_DEFAULT);
offset = ex.get(dsCfg.get("offset", String.class), Integer.class);
//skip offset number of valid resources
for (int skip = offset; skip > 0 && it.hasNext();) {
    Tag res = it.next();
    skip--;
}

// count of rows to get
rows = ex.get(dsCfg.get("limit", String.class), Integer.class);
if (rows <= 0) {
    rows = Integer.MAX_VALUE;
}

// populate the rv with resources
while (it.hasNext() && count < rows) {
    Tag tag = it.next();
    Resource p = tag.adaptTo(Resource.class);
    rv.add(p);
    count++;
}


final List<Resource> lst = rv;
@SuppressWarnings("unchecked")
DataSource datasource = new AbstractDataSource() {
    public Iterator<Resource> iterator() {
        Iterator<Resource> it = lst.iterator();
        return it;        
    }
};
request.setAttribute(DataSource.class.getName(), datasource);
addOthersAsRequestAttributes(dsCfg.getProperties(), request, "filters", "limit", "offset");

%>
<%!

void addOthersAsRequestAttributes(Map<String, Object> data, ServletRequest request, String... exclusions) {
	List<String> blacklisted = Arrays.asList(exclusions);
	for (Entry<String, Object> e : data.entrySet()) {
        String key = e.getKey();
        if (key.indexOf(":") >= 0) continue;
        if (blacklisted.indexOf(key) >= 0) continue;
        request.setAttribute(key, e.getValue().toString());
    }
	
}
%>
