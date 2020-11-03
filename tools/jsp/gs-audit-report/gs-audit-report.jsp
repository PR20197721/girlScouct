<%@page
        import="com.day.cq.replication.ReplicationStatus,
                com.day.cq.wcm.api.Page,
                com.day.cq.wcm.api.PageFilter,
                com.day.cq.wcm.msm.api.LiveRelationship,
                com.day.cq.wcm.msm.api.LiveRelationshipManager,
                org.apache.commons.lang3.StringUtils,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceResolver,
                org.girlscouts.common.components.GSEmailAttachment,
                org.girlscouts.common.osgi.service.GSEmailService,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                javax.jcr.RangeIterator,
                javax.jcr.nodetype.NodeType,
                javax.mail.internet.InternetAddress,
                java.util.*,
                java.util.regex.Pattern,
                java.util.stream.Stream" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    Logger auditlog = LoggerFactory.getLogger(this.getClass());
    //declarations
    org.girlscouts.web.osgi.component.GirlscoutsDnsProvider girlscoutsDnsProvider = sling.getService(org.girlscouts.web.osgi.component.GirlscoutsDnsProvider.class);
    auditlog.info("girlscoutsDnsProvider>>" + girlscoutsDnsProvider);
    final LiveRelationshipManager relationManager = resourceResolver.adaptTo(LiveRelationshipManager.class);
    String requestProto = request.getHeader("X-Forwarded-Proto");
    if (requestProto == null) {
        requestProto = "https";
    }
    //get hostname
    String hostName = requestProto + "://" + request.getServerName() + ":" + request.getServerPort();
    //set response as csv
    //response.setHeader("Content-Encoding", "UTF-8");
    //response.setContentType("text/csv; charset=UTF-8");
    //response.setHeader("Content-Disposition", "attachment; filename=GS Content Audit Report.csv");
    //set header 
    String header = "Template Page Author URL,Counil Folder,Page Exists,Council Page Author URL,Page Published,Prod Council Page URL,Page Inheritance,Image Component,Image Component Inheritance,Text Component,Text Component Inheritance,Text/Image Component,Text/Image Component Inheritance,Accordion Component,Accordion Component Inheritance,User Added Image Component,User Added Text Component,User Added Text/Image Component,User Added Accordion Component";
    //csv object
    StringBuilder csv = new StringBuilder();
    //header added
    csv.append(header + "\n");
    //template site rootpage
    final String templateSiteRoot = "/content/girlscouts-template";
    //councils root
    final String councilRoot = "/content";
    //get council sites list
    List<String> councilsList = getCouncilsList(councilRoot, resourceResolver);
//List<String> councilsList = new ArrayList<>();
//councilsList.add("/content/girlscouts-future");
//councilsList.add("/content/bdgsc");
//councilsList.add("/content/gsbadgerland");
//councilsList.add("/content/girlscoutsccc");
//councilsList.add("/content/girlscoutsalaska");

    auditlog.info("councilsList>>" + councilsList);

    //remove councils which are not required
    councilsList.removeIf(s -> s.equals("/content/girlscouts-template"));
    councilsList.removeIf(s -> s.equals("/content/go-gold"));
    councilsList.removeIf(s -> s.equals("/content/gsusa"));
	councilsList.removeIf(s -> s.equals("/content/gsbulkeditor-test"));
	councilsList.removeIf(s -> s.equals("/content/rollout-61"));
	councilsList.removeIf(s -> s.equals("/content/sandbox"));
	councilsList.removeIf(s -> s.equals("/content/rollout61-2"));
	councilsList.removeIf(s -> s.equals("/content/gstest"));
	councilsList.removeIf(s -> s.equals("/content/test3"));
	councilsList.removeIf(s -> s.equals("/content/test"));
	councilsList.removeIf(s -> s.equals("/content/seo-test-council"));
    councilsList.removeIf(s -> s.equals("/content/seo-permission-test-2"));
    councilsList.removeIf(s -> s.equals("/content/vtkcontent"));
    councilsList.removeIf(s -> s.equals("/content/seo-test-042516"));
    councilsList.removeIf(s -> s.equals("/content/lb-test"));
    councilsList.removeIf(s -> s.equals("/content/lb_test"));
    councilsList.removeIf(s -> s.equals("/content/lb_test1"));
    councilsList.removeIf(s -> s.equals("/content/pizza_party_test_lb"));
    councilsList.removeIf(s -> s.equals("/content/online-support-for-volunteers"));
    councilsList.removeIf(s -> s.equals("/content/sm-test"));
    councilsList.removeIf(s -> s.equals("/content/2017_honorees"));
    councilsList.removeIf(s -> s.equals("/content/our-camps"));
    councilsList.removeIf(s -> s.equals("/content/year-round-program"));
    councilsList.removeIf(s -> s.equals("/content/staff-directory-1"));
    councilsList.removeIf(s -> s.equals("/content/discover_the_wonders_test"));
    councilsList.removeIf(s -> s.equals("/content/digital-cookie"));
    councilsList.removeIf(s -> s.equals("/content/joinus-redirect"));
    councilsList.removeIf(s -> s.equals("/content/Camp20191"));
    councilsList.removeIf(s -> s.equals("/content/February8"));
    councilsList.removeIf(s -> s.equals("/content/TownHallScript"));
    councilsList.removeIf(s -> s.equals("/content/girlscouts-mobile"));
    councilsList.removeIf(s -> s.equals("/content/PatchProgramsCouncilsOwnAwards"));
    councilsList.removeIf(s -> s.equals("/content/campaign"));
    councilsList.removeIf(s -> s.equals("/content/test0"));
    councilsList.removeIf(s -> s.equals("/content/regent"));
    councilsList.removeIf(s -> s.equals("/content/mac"));
    //List templatesite pages
    List<String> templatePagesList = new ArrayList<>();
    Resource templateResource = resourceResolver.resolve(templateSiteRoot);
    //process start
    try {
        if (null != templateResource) {
            Page templatePageRoot = templateResource.adaptTo(Page.class);
            Iterator<Page> templatePageRootIterator = templatePageRoot.listChildren(new PageFilter(), true);
            while (templatePageRootIterator.hasNext()) {
                String pagePath = templatePageRootIterator.next().getPath();
                templatePagesList.add(pagePath);
            }
            //auditlog.info("templatePagesList>>" + templatePagesList);
            if (null != templatePagesList && !templatePagesList.isEmpty()) {
                for (String templatePage : templatePagesList) {
                    //Template Page Author URL
                    String templatePageAuthorUrl = hostName + "/editor.html" + templatePage + ".html";
                    Resource srcRes = resourceResolver.resolve(templatePage);
                    //iterating over each council
                    for (String council : councilsList) {
                        long startTime_OneCouncilPage = System.currentTimeMillis();//one councilpage mapped to one template page
                    	try {
                        //csv.append(templatePageAuthorUrl);//1
                        String pageExists = "";
                        String councilFolder = council.split("/")[2];

                        RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, council, null);
                        boolean hasValidRelationship = false;
                        if (relationIterator.hasNext()) {
                            Set<String> relations = new TreeSet<String>();
                            while (relationIterator.hasNext()) {
                                long startTime_getAllrelations = System.currentTimeMillis();
                                LiveRelationship relation = (LiveRelationship) relationIterator.next();
                                long endTime_getAllrelations = System.currentTimeMillis();
                                long timeTakenToGetAllrelations =    endTime_getAllrelations - startTime_getAllrelations;
                                auditlog.info("Time taken to get all relations-->"+timeTakenToGetAllrelations);
                                String targetPath = relation.getTargetPath();
                                relations.add(targetPath);
                            }
                            for (String targetPath : relations) {
                                Resource targetResource = resourceResolver.getResource(targetPath);
                                if (targetResource != null && !targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                    //page is available
                                    pageExists = "Yes";
                                    //page has valid live relationship
                                    hasValidRelationship=true;
                                    //council author url
                                    String councilPageAuthorUrl = hostName + "/editor.html" + targetPath + ".html";
                                    //page publish status
                                    boolean publishStatus = getPublishedStatus(targetPath, resourceResolver);
                                    //prod publisher url
                                    String prodCouncilUrl = "";
                                    if (councilFolder.equals("girlscouts-future")) {
                                        prodCouncilUrl = "http://future.girlscouts.org" + targetPath + ".html";

                                    } else {
                                        prodCouncilUrl = girlscoutsDnsProvider.getDns(targetPath);
                                        prodCouncilUrl = StringUtils.replace(prodCouncilUrl, "http://preview", "https://www");
                                        prodCouncilUrl = prodCouncilUrl + targetPath + ".html";
                                    }
                                    auditlog.info("prodCouncilUrl>>" + prodCouncilUrl);
                                    String pagePublishState = "";
                                    if (publishStatus) {
                                        pagePublishState = "Yes," + prodCouncilUrl + ",";
                                    } else {
                                        pagePublishState = "No,,";
                                    }
                                    csv.append(templatePageAuthorUrl + "," + councilFolder + "," + pageExists + "," + councilPageAuthorUrl + "," + pagePublishState);//6
                                    String pageInheritance = "";
                                    //get custom components present on the page
                                    long startTime_customComponentsSet = System.currentTimeMillis();
                                    Set<String> customComponentsSet = categorizeRelationComponents(targetResource, resourceResolver);
                                    long endTime_customComponentsSet = System.currentTimeMillis();
									auditlog.info("Time to get customComponentsSet" + (endTime_customComponentsSet-startTime_customComponentsSet));
                                    auditlog.info("customComponentsSet>>" + customComponentsSet);
                                    String userImgCmp = "No";
                                    String userTxtImgCmp = "No";
                                    String userTxtCmp = "No";
                                    String userAccCmp = "No";
                                    for (String customComponent : customComponentsSet) {
                                        Resource customCmpRes = resourceResolver.getResource(customComponent);
                                        Node customCmpNode = customCmpRes.adaptTo(Node.class);
                                        String customComponentName = StringUtils.substringAfterLast(customComponent, "/");
                                        boolean imageInsideTxtImg = Pattern.matches("textimage.*", StringUtils.substringAfterLast(StringUtils.substringBeforeLast(customComponent, "/image"), "/"));
                                        if (Pattern.matches("image.*", customComponentName) && customCmpNode.hasProperty("sling:resourceType") && !imageInsideTxtImg) {
                                            userImgCmp = "Yes";
                                        } else if (Pattern.matches("textimage.*", customComponentName) && customCmpNode.hasProperty("sling:resourceType")) {
                                            userTxtImgCmp = "Yes";
                                        } else if (Pattern.matches("text.*", customComponentName) && customCmpNode.hasProperty("sling:resourceType")) {
                                            userTxtCmp = "Yes";
                                        } else if (Pattern.matches("accordion.*", customComponentName) && customCmpNode.hasProperty("sling:resourceType")) {
                                            userAccCmp = "Yes";
                                        }
                                    }
                                    Node node = targetResource.adaptTo(Node.class);
                                    if (!node.hasProperty("breakInheritance") || (node.hasProperty("breakInheritance") && (node.getProperty("breakInheritance") == null || node.getProperty("breakInheritance").getBoolean() == Boolean.FALSE))) {
                                        if (node.hasNode("jcr:content")) {
                                            Node jcrContent = node.getNode("jcr:content");
                                            if (!jcrContent.hasProperty("breakInheritance") || (jcrContent.hasProperty("breakInheritance") && (jcrContent.getProperty("breakInheritance") == null || jcrContent.getProperty("breakInheritance").getBoolean() == Boolean.FALSE))) {
                                                pageInheritance = "Yes";
                                                String imgComponent = "";
                                                String txtImgComponent = "";
                                                String txtComponent = "";
                                                String accordionComponent = "";
                                                String imgComponentInheritance = "";
                                                String txtImgComponentInheritance = "";
                                                String txtComponentInheritance = "";
                                                String accordionComponentInheritance = "";
                                                csv.append(pageInheritance);//7
                                                long startTime_getComponentRelations = System.currentTimeMillis();
                                                Map<String, Set<String>> componentRelations = getComponentRelations(srcRes, targetPath, resourceResolver);
                                                long endTime_getComponentRelations = System.currentTimeMillis();
                                                auditlog.info("Time to fetch ComponentRelations --->"+(endTime_getComponentRelations - startTime_getComponentRelations));
                                                long startTime_getComponentInheritanceMap = System.currentTimeMillis();

                                                Map<String, String> cmpMap = getComponentInheritanceMap(componentRelations, targetPath, resourceResolver);
                                                long endTime_getComponentInheritanceMap = System.currentTimeMillis();
												auditlog.info("Time to fetch ComponentInheritanceMap --->"+(endTime_getComponentInheritanceMap - startTime_getComponentInheritanceMap));
                                                if (null != cmpMap && !cmpMap.isEmpty()) {
                                                    for (Map.Entry<String, String> entry : cmpMap.entrySet()) {
                                                        Resource inheritedCmpRes = resourceResolver.getResource(entry.getKey());
                                                        Node inheritedCmpNode = inheritedCmpRes.adaptTo(Node.class);
                                                        String inheritedComponent = StringUtils.substringAfterLast(entry.getKey(), "/");
                                                        boolean imgInsideTxtImg = Pattern.matches("textimage.*", StringUtils.substringAfterLast(StringUtils.substringBeforeLast(entry.getKey(), "/image"), "/"));
                                                        if (Pattern.matches("image.*", inheritedComponent) && inheritedCmpNode.hasProperty("sling:resourceType") && !imgInsideTxtImg) {
                                                            imgComponent = "Yes";
                                                            if (imgComponentInheritance.equals("")) {
                                                                imgComponentInheritance = entry.getValue();
                                                            }
                                                            if (!entry.getValue().equals("Yes")) {
                                                                imgComponentInheritance = "No";
                                                            }
                                                        } else if (Pattern.matches("textimage.*", inheritedComponent) && inheritedCmpNode.hasProperty("sling:resourceType")) {
                                                            txtImgComponent = "Yes";
                                                            if (txtImgComponentInheritance.equals("")) {
                                                                txtImgComponentInheritance = entry.getValue();
                                                            }
                                                            if (!entry.getValue().equals("Yes")) {
                                                                txtImgComponentInheritance = "No";
                                                            }
                                                        } else if (Pattern.matches("text.*", inheritedComponent) && inheritedCmpNode.hasProperty("sling:resourceType")) {
                                                            txtComponent = "Yes";
                                                            if (txtComponentInheritance.equals("")) {
                                                                txtComponentInheritance = entry.getValue();
                                                            }
                                                            if (!entry.getValue().equals("Yes")) {
                                                                txtComponentInheritance = "No";
                                                            }
                                                        } else if (Pattern.matches("accordion.*", inheritedComponent) && inheritedCmpNode.hasProperty("sling:resourceType")) {
                                                            accordionComponent = "Yes";
                                                            if (accordionComponentInheritance.equals("")) {
                                                                accordionComponentInheritance = entry.getValue();
                                                            }
                                                            if (!entry.getValue().equals("Yes")) {
                                                                accordionComponentInheritance = "No";
                                                            }
                                                        }

                                                    }

                                                } else {
                                                    imgComponent = "No";
                                                    imgComponentInheritance = "No";
                                                    txtImgComponent = "No";
                                                    txtImgComponentInheritance = "No";
                                                    txtComponent = "No";
                                                    txtComponentInheritance = "No";
                                                    accordionComponent = "No";
                                                    accordionComponentInheritance = "No";

                                                }
                                                if (imgComponent.equals("")) {
                                                    imgComponent = "No";
                                                    imgComponentInheritance = "No";
                                                }
                                                if (txtImgComponent.equals("")) {
                                                    txtImgComponent = "No";
                                                    txtImgComponentInheritance = "No";
                                                }
                                                if (txtComponent.equals("")) {
                                                    imgComponent = "No";
                                                    txtComponentInheritance = "No";
                                                }
                                                if (accordionComponent.equals("")) {
                                                    accordionComponent = "No";
                                                    accordionComponentInheritance = "No";
                                                }
                                                csv.append("," + imgComponent + "," + imgComponentInheritance + "," + txtComponent + "," + txtComponentInheritance + "," + txtImgComponent + "," + txtImgComponentInheritance + "," + accordionComponent + "," + accordionComponentInheritance + "," + userImgCmp + "," + userTxtCmp + "," + userTxtImgCmp + "," + userAccCmp + "\n");
                                            } else {
                                                pageInheritance = "No";
                                                csv.append(pageInheritance + ",,,,,,,,,,,," + "\n");
                                            }
                                        }
                                    } else {
                                        pageInheritance = "No";
                                        csv.append(pageInheritance + ",,,,,,,,,,,," + "\n");
                                    }

                                } else {
                                    //pageExists = "No";
                                    //csv.append(templatePageAuthorUrl + "," + councilFolder + "," + pageExists + ",,,,,,,,,,,,,,,," + "\n");//3
                                }
                            }
                            if(!hasValidRelationship){
										pageExists = "No";
										csv.append(templatePageAuthorUrl + "," + councilFolder + "," + pageExists + ",,,,,,,,,,,,,,,," + "\n");//3
							}

                        } else {
                            pageExists = "NA";
                            csv.append(templatePageAuthorUrl + "," + councilFolder + "," + pageExists + ",,,,,,,,,,,,,,,," + "\n");//3
                        }
                    } catch (Exception e) {
                    	auditlog.info("Exception occurred: ", e);
                    }
                       long endTime_OneCouncilPage = System.currentTimeMillis();//one councilpage mapped to one template page
					   auditlog.info("Time for relationship discovery for one page--->" + (endTime_OneCouncilPage - startTime_OneCouncilPage)); 

                   }
                }
            }
        }
        //out.println(csv.toString().trim());
        GSEmailAttachment attachment = new GSEmailAttachment("GS Content Audit Report.csv", csv.toString().trim(), null, GSEmailAttachment.MimeType.TEXT_CSV);
        Set<GSEmailAttachment> attachments = new HashSet<>();
        attachments.add(attachment);
        ArrayList<String> emailRecipients = new ArrayList<>();
        StringBuilder msgBody = new StringBuilder();
        GSEmailService gsEmailService = sling.getService(GSEmailService.class);
        msgBody.append("This is the GS audit report");
        emailRecipients.add(new InternetAddress("rohini.nadendla@gds.ey.com").toString());
        emailRecipients.add(new InternetAddress("SSGattupally@girlscouts.org").toString());
        emailRecipients.add(new InternetAddress("khalil.ganiga@gds.ey.com").toString());
        emailRecipients.add(new InternetAddress("Saurav.Rawat@gds.ey.com").toString());
        auditlog.info("msgBody:" + msgBody + "emailRecipients:" + emailRecipients.get(0));
        gsEmailService.sendEmail("Gs Audit Report", emailRecipients, msgBody.toString(), attachments);
        auditlog.info("MAIL IS SENT");

    } catch (Exception e) {
        auditlog.info("Exception occured", e);
    }
%>
<%!
    public List<String> getCouncilsList(String root, ResourceResolver resourceResolver) {
        List<String> counclis;
        Resource parentResource = resourceResolver.getResource(root);
        Iterator<Resource> resource = parentResource.listChildren();
        List<String> councils = new ArrayList<String>();
        while (resource.hasNext()) {
            Resource res = resource.next();
            if (res.adaptTo(Page.class) != null) {
                councils.add(res.getPath());
            }
        }
        return councils;

    }

    private Map<String, Set<String>> getComponentRelations(Resource srcRes, String targetPath, ResourceResolver rr) {
        Logger complog = LoggerFactory.getLogger(this.getClass());
        Map<String, Set<String>> componentRelationsMap = new HashMap<String, Set<String>>();
        Set<String> srcComponents = getComponents(srcRes);
        if (srcComponents != null && srcComponents.size() > 0) {
            for (String component : srcComponents) {
                Resource componentRes = rr.resolve(component);
                if (componentRes != null && !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    //System.err.println("*********Looking up relations for " + componentRes);
                    try {
                        final LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
                        //System.err.println("relationManager "+relationManager);
                         long startTime_componentLiveRelation = System.currentTimeMillis();
                        RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes, targetPath, null);
                        long endTime_componentLiveRelation = System.currentTimeMillis();
                        complog.info("Time taken for component level relationships relationManager-query--->"+(endTime_componentLiveRelation - startTime_componentLiveRelation));
                        if (relationIterator.hasNext()) {
                            Set<String> componentRelations = new TreeSet<String>();
                            while (relationIterator.hasNext()) {
                                LiveRelationship relation = (LiveRelationship) relationIterator.next();
                                String relationPath = relation.getTargetPath();
                                Resource targetPathResource = rr.resolve(relationPath);
                                if (!targetPathResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                    //complog.info("EXISTING::"+ relationPath);
                                    componentRelations.add(relationPath);
                                }
                            }
                            componentRelationsMap.put(component, componentRelations);
                        }
                    } catch (Exception e) {
                        complog.info("error inmethod  getComponentRelations---->>",e);
                    }
                }
            }
        }
        return componentRelationsMap;

    }

    private Set<String> getComponents(Resource srcRes) {
        Set<String> components = null;
        try {
            components = new HashSet<String>();
            Resource content = srcRes.getChild("jcr:content");
            traverseNodeForComponents(content, components);
        } catch (Exception e) {
        }
        return components;
    }

    private Set<String> getComponentSet(Resource srcRes) {
        Set<String> components = null;
        Set<String> componentSet = new HashSet<>();
        try {
            components = new HashSet<String>();
            Resource content = srcRes.getChild("jcr:content");
            traverseNodeForComponents(content, components);
            for (String str : components) {
                str = StringUtils.substringAfter(str, str.split("/")[2]);
                componentSet.add(str);
            }
        } catch (Exception e) {
        }
        return componentSet;
    }

    private void traverseNodeForComponents(Resource srcRes, Set<String> components) {
        if (srcRes != null) {
            components.add(srcRes.getPath());
            Iterable<Resource> children = srcRes.getChildren();
            if (children != null) {
                Iterator<Resource> it = children.iterator();
                while (it.hasNext()) {
                    traverseNodeForComponents(it.next(), components);
                }
            }
        }
    }

    public boolean getPublishedStatus(String path, ResourceResolver rr) {
        Resource resource = rr.resolve(path);
        Boolean activated;
        ReplicationStatus status = null;
        activated = false;
        if (resource != null) {
            try {
                Page page = resource.adaptTo(Page.class);
                status = page.adaptTo(ReplicationStatus.class);
            } catch (Exception e) {
                //LOG.debug(e.getMessage(), e);
            }
            if (status != null) {
                activated = status.isActivated();
            }
        }
        return activated;
    }

    private Map<String, String> getComponentInheritanceMap(Map<String, Set<String>> componentRelations, String targetPath, ResourceResolver rr) {
        Logger compInheritLog = LoggerFactory.getLogger(this.getClass());
        boolean inheritanceBroken = false;
        String inheritanceStatus = "No";
        Map<String, String> map = new HashMap<>();
        String componentArray[] = {"image", "text", "accordion"};
        if (componentRelations != null && componentRelations.size() > 0) {
            for (String component : componentRelations.keySet()) {
                //compInheritLog.info("component relation value:"+ componentRelations.get(component));
                String cmpRes = "";
                if (componentRelations.get(component) != null && componentRelations.get(component).size() > 0) {
                    for (String targetComponentPath : componentRelations.get(component)) {
                        cmpRes = StringUtils.substringAfterLast(targetComponentPath, "/");
                        if (Stream.of(componentArray).anyMatch(cmpRes::startsWith)) {
                            inheritanceBroken = isInheritanceBroken(targetPath, componentRelations, component, rr);
                            inheritanceStatus = inheritanceBroken == true ? "No" : "Yes";
                            map.put(targetComponentPath, inheritanceStatus);
                        }
                    }
                }
            }
        }
        return map;
    }

    private boolean isInheritanceBroken(String targetPath, Map<String, Set<String>> componentRelations, String component, ResourceResolver rr) {
        //System.err.println("*********Checking inheritance for " + component + " in " + targetPath);
        Resource componentRes = rr.resolve(component);
        if (componentRes != null && !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            try {
                boolean relationShipExists = false;
                Set<String> relations = componentRelations.get(component);
                for (String relationPath : relations) {
                    if (relationPath.startsWith(targetPath)) {
                        relationShipExists = true;
                        Resource targetComponentRes = rr.resolve(relationPath);
                        if (targetComponentRes != null && !targetComponentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                            try {
                                Node componentNode = targetComponentRes.adaptTo(Node.class);
                                if (componentNode != null) {
                                    NodeType[] mixinTypes = componentNode.getMixinNodeTypes();
                                    if (mixinTypes != null && mixinTypes.length > 0) {
                                        for (NodeType mixinType : mixinTypes) {
                                            if (mixinType.isNodeType("cq:LiveSyncCancelled")) {
                                                return true;
                                            }
                                        }
                                    } else {
                                        return true;
                                    }
                                }
                            } catch (Exception e) {
                            }
                        } else {
                            return true;
                        }
                    }
                }
                if (!relationShipExists) {
                    return true;
                }
            } catch (Exception e1) {
            }
        }
        return false;
    }

    public Set<String> categorizeRelationComponents(Resource relationPageResource, ResourceResolver rr) {
        Logger testLog = LoggerFactory.getLogger(this.getClass());
        Map<String, Set<String>> relationComponents = new HashMap<String, Set<String>>();
        Set<String> customComponents = new HashSet<String>();
        Set<String> activeLiveSyncComponents = new HashSet<String>();
        Set<String> inactiveLiveSyncComponents = new HashSet<String>();
        categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, relationPageResource.getChild("jcr:content"), true);
        relationComponents.put("customComponents", customComponents);
        // relationComponents.put(RELATION_INHERITED_COMPONENTS, activeLiveSyncComponents);
        //relationComponents.put(RELATION_CANC_INHERITANCE_COMPONENTS, inactiveLiveSyncComponents);
        testLog.info("Custome Components >>" + relationComponents);
        return customComponents;
    }

    private void categorizeRelationComponents(Set<String> customComponents, Set<String> activeLiveSyncComponents, Set<String> inactiveLiveSyncComponents, Resource resource, boolean isParentInherited) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        try {
            if (resource != null && resource.hasChildren()) {
                Iterator<Resource> it = resource.getChildren().iterator();
                while (it.hasNext()) {
                    Resource childResource = it.next();
                    Node node = childResource.adaptTo(Node.class);
                    if ("nt:unstructured".equals(node.getPrimaryNodeType().getName())) {
                        try {
                            if (!isParentInherited) {
                                log.info("Component {} has cancelled inheritance because parent has cancelled inheritance.", childResource.getPath());
                                inactiveLiveSyncComponents.add(childResource.getPath());
                                categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, false);
                            } else {
                                if (childResource.isResourceType("wcm/msm/components/ghost")) {
                                    log.info("Component {} is a deleted inherited component.", childResource.getPath());
                                    inactiveLiveSyncComponents.add(childResource.getPath());
                                    categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, false);
                                } else {
                                    Node childNode = childResource.adaptTo(Node.class);
                                    if (childNode.isNodeType("cq:LiveSyncCancelled") && !isParsys(childNode)) {
                                        log.info("Component {} has cancelled inheritance.", childResource.getPath());
                                        inactiveLiveSyncComponents.add(childResource.getPath());
                                        categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, false);
                                    } else {
                                        if (childNode.isNodeType("cq:LiveRelationship")) {
                                            log.info("Component {} is inherited.", childResource.getPath());
                                            activeLiveSyncComponents.add(childResource.getPath());
                                            categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, true);
                                        } else {
                                            log.info("Component {} is custom.", childResource.getPath());
                                            customComponents.add(childResource.getPath());
                                            categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, true);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("PageReplicationUtil encountered error: ", e);
                        }
                    } else {
                        log.info("Skipping node {} since its not of type nt:unstructured.", childResource.getPath());
                        categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents, childResource, true);
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("PageReplicationUtil encountered error: ", e);
        }
    }

    private boolean isParsys(Node childNode) {
        try {
            String resourceType = childNode.getProperty("sling:resourceType").getString();
            return resourceType.equals("girlscouts-common/components/styled-parsys") || resourceType.equals("girlscouts/components/styled-parsys") || resourceType.equals("foundation/components/parsys");
        } catch (Exception e) {
        }
        return false;
    }
%>