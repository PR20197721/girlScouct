<%@ page import="com.day.cq.commons.servlets.HtmlStatusResponseHelper,
                 org.apache.sling.api.servlets.HtmlResponse,
                 java.util.Enumeration,
                 org.apache.sling.api.resource.Resource,
                 javax.jcr.Node,
                 com.day.cq.commons.jcr.JcrUtil,
                 javax.jcr.Property,
                 javax.jcr.Session,
                 com.day.text.Text,
				 com.day.cq.tagging.TagManager,
				 java.util.regex.Pattern,
				 java.util.regex.Matcher,
				 java.util.ArrayList,
				 javax.jcr.ValueFormatException,
				 java.util.HashMap,
				 javax.jcr.Value,
				 org.girlscouts.web.events.search.GSDateTime,
				 org.girlscouts.web.events.search.GSDateTimeFormat,
				 org.girlscouts.web.events.search.GSDateTimeFormatter,
				 org.girlscouts.web.events.search.GSDateTimeZone" %>
<%@ page session="false" %>
<%
%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%
%><sling:defineObjects/><%
    HtmlResponse htmlResponse = null;
    try {
        Session session = resourceResolver.adaptTo(Session.class);
        boolean updated = false;
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            try {
                String path = (String) names.nextElement();
                if( path.startsWith("/")) {
                    boolean isDelete = false;
                    if( path.indexOf("@Delete") != -1) {
                        isDelete = true;
                        path = path.substring(0, path.indexOf("@Delete"));
                    }

                    String value = request.getParameter(path);
                    Resource r = resourceResolver.getResource(path);
                    String propertyName = Text.getName(path);
                    if(propertyName.equals("cq:tags-categories") || propertyName.equals("cq:tags-progLevel") || propertyName.equals("start-date") || propertyName.equals("start-time") || propertyName.equals("end-date") || propertyName.equals("end-time") || propertyName.equals("regOpen-date") || propertyName.equals("regOpen-time") || propertyName.equals("regClose-date") || propertyName.equals("regClose-time")){
                    	r = resourceResolver.getResource(path.substring(0,path.lastIndexOf("-")));
                    }
                    String[] values = null;
                    //GS - Create new tags if they didn't exist before
                    TagManager tm = resourceResolver.adaptTo(TagManager.class);
                    if(propertyName.equals("cq:tags")){
                    	String[] tagValues = value.split(";");
                    	ArrayList<String> valueList = new ArrayList<String>();
                    		if(tagValues.length > 0){
                    		for(String tagVal : tagValues){
                    			String tagTitle = tagVal.trim();
                    			 try{
                    				String tagID = createId(tagTitle, r.getPath(), "forms_documents");
                    				if(tagID != null){
		                    			if(null == tm.resolve(tagID)){
		                    				if(tm.canCreateTag(tagID)){
		                    					tm.createTag(tagID,tagTitle,"",true);
		                    					valueList.add(tagID);
		                    				}
		                    			}else{
		                    				valueList.add(tagID);
		                    			}
                    				}
                    			}catch(Exception e){
                    				System.err.println("GSBulkEditor - Failed to Create Tag");
                    			} 
                    		}
                    	}
                    	values = valueList.toArray(new String[0]);
                    }else if(propertyName.equals("cq:tags-categories") || propertyName.equals("cq:tags-progLevel")){
                    	String tagFolder = propertyName.substring(propertyName.lastIndexOf("-")+1,propertyName.length());
						if(tagFolder.equals("progLevel")){
							tagFolder = "program-level";
						}
						String[] tagValues = value.split(";");
						ArrayList<String> valueList = new ArrayList<String>();
						if (r != null) {
							Property tagsProp = r.adaptTo(Property.class);
							Value[] tagsVals = tagsProp.getValues();
							if(tagsVals.length > 0){
								for(Value existing : tagsVals){
									String existingStr = existing.getString();
									if(tagFolder.equals("program-level")){
										if(existingStr.matches("^.*:" + "categories" + "/.*$")){
											valueList.add(existingStr);
										}
									}else if(tagFolder.equals("categories")){
										if(existingStr.matches("^.*:" + "program-level" + "/.*$")){
											valueList.add(existingStr);
										}
									}
								}
							}
						}
                    	if(tagValues.length > 0){
                    		for(String tagVal : tagValues){
                    			String tagTitle = tagVal.trim();
                    			 try{
                    				String tagID = createId(tagTitle, r.getPath(), tagFolder);
                    				if(tagID != null){
		                    			if(null == tm.resolve(tagID)){
		                    				if(tm.canCreateTag(tagID)){
		                    					tm.createTag(tagID,tagTitle,"",true);
		                    					valueList.add(tagID);
		                    				}
		                    			}else{
		                    				valueList.add(tagID);
		                    			}
                    				}
                    			}catch(Exception e){
                    				System.err.println("GSBulkEditor - Failed to Create Tag");
                    			} 
                    		}
                    	}
                    	values = valueList.toArray(new String[0]);
                    }else if(propertyName.equals("start-date") || propertyName.equals("end-date") || propertyName.equals("regOpen-date") || propertyName.equals("regClose-date")){
                    	if(r != null){
                    		Property dateProp = r.adaptTo(Property.class);
	                    	String dateString = value;
	                    	String timeString = dateProp.getString().substring(dateProp.getString().indexOf("T"));
	                    	String dateTimeString = dateString + timeString;
	                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'HH:mm:ss.SSSZ");
	                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
	                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	                    	value = dtfOut.print(dt);
                    	}else{
    						String dateString = value;
    						String timeString = "T00:00:00.000";
                        	String dateTimeString = dateString + timeString;
                        	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'HH:mm:ss.SSS");
                        	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
                        	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
                        	value = dtfOut.print(dt);
                    	}
                    }else if(propertyName.equals("start-time") || propertyName.equals("end-time") || propertyName.equals("regOpen-time") || propertyName.equals("regClose-time")){
                    	if(r != null){
                    		Property dateProp = r.adaptTo(Property.class);
	                    	String datetimeString = dateProp.getString();
	                    	String timeString = value;
	                    	String dateString = dateProp.getString().substring(0, dateProp.getString().indexOf("T"));
	                    	String dateTimeString = dateString + "T" + timeString + " -05:00";
	                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm a Z");
	                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
	                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	                    	value = dtfOut.print(dt);
                    	}else{
    						String timeString = value;
                        	String dateString = "2017-01-01";
                        	String dateTimeString = dateString + "T" + timeString + " -05:00";
                        	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm a Z");
                        	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
                        	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
                        	value = dtfOut.print(dt);
                    	}
                    }else if(propertyName.equals("timezone")){
                    	value = "()(" + value + ")";
                    	String rowPath = Text.getRelativeParent(path, 1);
                        Resource rowResource = resourceResolver.getResource(rowPath);
                        if (rowResource != null) {
                            //add property to node
                            Node updatedNode = rowResource.adaptTo(Node.class);
                            if(updatedNode != null ) {
		    					GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		    					GSDateTimeFormatter dtfOutZone = GSDateTimeFormat.forPattern("ZZ");
		    					if(updatedNode.hasProperty("start")){
		    						try{
		    							String startDate = updatedNode.getProperty("start").getString();
		    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
		    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
		    						    startDateTime = startDateTime.withZone(dtz);
		    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
		    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
		    						    updatedNode.setProperty("start",startDate);
		    						}catch(Exception e){
		    							e.printStackTrace();
		    						}
		    					}
		    					if(updatedNode.hasProperty("end")){
		    						try{
		    							String startDate = updatedNode.getProperty("end").getString();
		    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
		    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
		    						    startDateTime = startDateTime.withZone(dtz);
		    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
		    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
		    						    updatedNode.setProperty("end",startDate);
		    						}catch(Exception e){
		    							e.printStackTrace();
		    						}
		    					}
		    					if(updatedNode.hasProperty("regOpen")){
		    						try{
		    							String startDate = updatedNode.getProperty("regOpen").getString();
		    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
		    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
		    						    startDateTime = startDateTime.withZone(dtz);
		    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
		    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
		    						    updatedNode.setProperty("regOpen",startDate);
		    						}catch(Exception e){
		    							e.printStackTrace();
		    						}
		    					}
		    					if(updatedNode.hasProperty("regClose")){
		    						try{
		    							String startDate = updatedNode.getProperty("regClose").getString();
		    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
		    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
		    						    startDateTime = startDateTime.withZone(dtz);
		    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
		    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
		    						    updatedNode.setProperty("regClose",startDate);
		    						}catch(Exception e){
		    							e.printStackTrace();
		    						}
		    					}
                            }
                        }
                    }

                    if(propertyName.equals("cq:tags-categories") || propertyName.equals("cq:tags-progLevel") || propertyName.equals("start-date") || propertyName.equals("start-time") || propertyName.equals("end-date") || propertyName.equals("end-time") || propertyName.equals("regOpen-date") || propertyName.equals("regOpen-time") || propertyName.equals("regClose-date") || propertyName.equals("regClose-time")){
                    	propertyName = propertyName.substring(0,propertyName.lastIndexOf("-"));
                    }
                    
                    if (r == null) {
                        //resource does not exist. 2 cases:
                        // - maybe it is a non existing property? property has to be created
                        // - maybe it is a new row? node has to be created first
                        String rowPath = Text.getRelativeParent(path, 1);
                        Resource rowResource = resourceResolver.getResource(rowPath);
                        if (rowResource != null) {
                            //add property to node
                            Node rowNode = rowResource.adaptTo(Node.class);
                            if(rowNode != null ) {
                                rowNode.setProperty(propertyName,value);
                                updated = true;
                            }
                        } else {
                            //create node and add property
                            String parentPath = Text.getRelativeParent(path, 2);
                            Resource parentResource = resourceResolver.getResource(parentPath);
                            if (parentResource != null) {
                                Node parentNode = parentResource.adaptTo(Node.class);
                                if (parentNode != null) {
                                    String nodeToCreateName = Text.getName(rowPath);
                                    Node rowNode = parentNode.addNode(nodeToCreateName);
                                    rowNode.setProperty(propertyName,value);
                                    updated = true;
                                }
                            }
                        }
                    } else {
                        if( isDelete ) {
                            Node n = r.adaptTo(Node.class);
                            if( n != null) {
                                n.remove();
                                updated = true;
                            }
                        } else {
                            //path should already be the property path
                            Property p = r.adaptTo(Property.class);
                            if (p != null) {
                            	//multi-valued properties
                            	if(values != null){
                                	p.setValue(values);
                            	}else{
                            		try{
                            			p.setValue(value);
                            		}catch(ValueFormatException e){
                            			values = new String[1];
                            			values[0] = value;
                            			p.setValue(values);
                            		}
                            	}
                                updated = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        if (updated) {
            session.save();
        }

        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                "Data saved");
    } catch (Exception e) {
        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                "Error while saving data", e.getMessage());
    }

    htmlResponse.send(response, true);
%>

<%!
String createId(String tag, String path, String folder){
	HashMap<String,String> specialCouncils = new HashMap<String,String>();
	specialCouncils.put("southern-appalachian","girlscoutcsa");
	specialCouncils.put("NE_Texas","NE_Texas");
	specialCouncils.put("nc-coastal-pines-images-","girlscoutsnccp");
	specialCouncils.put("wcf-images","gswcf");
	specialCouncils.put("oregon-sw-washington-","girlscoutsosw");
	specialCouncils.put("dxp","girlscouts-dxp");
	
	String councilRoot = "";
	String tagName = tag.trim().toLowerCase().replaceAll(" ","_").replaceAll("[^a-z0-9_]","_");
	Pattern pDam = Pattern.compile("^/content/dam/([^/]{1,})/*.*$");
	Matcher mDam = pDam.matcher(path);
	Pattern pContent = Pattern.compile("^/content/([^/]{1,})/*.*$");
	Matcher mContent = pContent.matcher(path);
	if(mDam.matches()){
		councilRoot = mDam.group(1);
    	if(specialCouncils.containsKey(councilRoot)){
    		councilRoot = specialCouncils.get(councilRoot);
    	}
    	if(councilRoot.startsWith("girlscouts-")){
    		councilRoot = councilRoot.replace("girlscouts-","");
    	}
		return councilRoot + ":" + folder + "/" + tagName;
	}else if(mContent.matches()){
		councilRoot = mContent.group(1);
    	if(specialCouncils.containsKey(councilRoot)){
    		councilRoot = specialCouncils.get(councilRoot);
    	}
		return councilRoot + ":" + folder + "/" + tagName;
	}else{
		return null;
	}
}
%>
