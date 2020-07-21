<%@page import="org.w3c.dom.traversal.NodeIterator"%>
<%@page
	import="com.day.cq.wcm.api.WCMMode, 
			org.apache.commons.lang3.StringUtils,
			org.apache.sling.api.resource.Resource, 
			java.util.Iterator,
			org.apache.commons.codec.binary.Base64,
			java.lang.StringBuilder, 
			javax.jcr.Session, 
			javax.jcr.PathNotFoundException,
			javax.jcr.Node,
			org.apache.sling.settings.SlingSettingsService,
			org.slf4j.Logger,
			org.slf4j.LoggerFactory,
			java.util.List,
			java.util.ArrayList"%>

<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="common.components.accordion" />

<%
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Resource children = resource.getChild("children");
	String accordionIndex = "";
	String accordionName = resource.getName();
	Session session = resourceResolver.adaptTo(Session.class);
	SlingSettingsService slingeSettings = sling
			.getService(org.apache.sling.settings.SlingSettingsService.class);
	boolean isAuthor = slingeSettings.getRunModes().contains("author");
	List<String> childParsysList = new ArrayList<>();
	if (accordionName.contains("_") && accordionName.length() > accordionName.indexOf('_') + 1) {
		accordionIndex = accordionName.substring(accordionName.indexOf('_') + 1);
	}

	if (children != null && !children.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		Iterator<Resource> items = children.listChildren();
		if (items != null && items.hasNext()) {
%>
<dl class="accordion accordionComponent" data-accordion>
	<%
		StringBuilder script = new StringBuilder();
				while (items.hasNext()) {
					Node accordion = items.next().adaptTo(Node.class);
					String achorField = "";
					String idField = "";
					String nameField = "";
					if (accordion.hasProperty("anchorField")) {
						achorField = accordion.getProperty("anchorField").getString();
					}
					if (accordion.hasProperty("idField")) {
						idField = accordion.getProperty("idField").getString();
					}
					if (accordion.hasProperty("nameField")) {
						nameField = accordion.getProperty("nameField").getString();
					}
					String parsys = "accordion";
					if (!StringUtils.isBlank(accordionIndex)) {
						parsys += "_" + accordionIndex;
					}
					parsys += "_parsys_";

					// Check if the node exists for the index.  Otherwise fallback to the ID if available.
					if (!StringUtils.isBlank(idField)) {
						parsys += idField;
					} else {
						try {
							//resource.adaptTo(Node.class).getNode(parsys + accordion.getName());
							parsys += accordion.getName();
						} catch (PathNotFoundException pnfe) {
							logger.error("Exception occured "+pnfe);
						}
					}

					String parsysIdentifier = resource.getPath() + "/" + parsys;
					childParsysList.add(parsys);
					if (isAuthor) {
						try {
							Resource parsysRes = resourceResolver.getResource(parsysIdentifier);
							if (null != parsysRes) {
								Node parNode = parsysRes.adaptTo(Node.class);
								javax.jcr.NodeIterator parNodeItr = parNode.getNodes();
								while (parNodeItr.hasNext()) {
									Node cNode = parNodeItr.nextNode();
									decodeAccordionContent(cNode, session,logger);
								}
							}
						} catch (Exception e) {
							logger.error("Exception occured" + e);
						}
					}
	%>

	<dt class="accordionComponentHeader" style="clear: both"
		id="<%=achorField%>" data-parsys-identifier="<%=parsysIdentifier%>">
		<h6 class="accordionComponentLabel"><%=nameField%></h6>
		<div class="accordionComponentSwitch"></div>
		<div class="clr" style="clear: both"></div>
	</dt>
	<dd class="accordion-navigation">
		<div class="content" id="<%=parsys%>">
			<cq:include path="<%=parsys%>"
				resourceType="foundation/components/parsys" />
		</div>
	</dd>
	<%
		}
	%>
</dl>
<%
	} else {
%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%
	}
	} else {
%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%
	}
%>
<%
	if (isAuthor) {
		try {
			Node accordionNode = resource.adaptTo(Node.class);
			if (null != accordionNode) {
				javax.jcr.NodeIterator nodeItr = accordionNode.getNodes();
				while (nodeItr.hasNext()) {
					Node accChildNode = nodeItr.nextNode();
					if (!accChildNode.getName().equals("children")) {
						if (!childParsysList.contains(accChildNode.getName())) {
							javax.jcr.NodeIterator contenNode = accChildNode.getNodes();
							while (contenNode.hasNext()) {
								Node cNode = contenNode.nextNode();
								encodeAccordionContent(cNode, session,logger);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}

	}
%>
<%!void encodeAccordionContent(Node cnode, Session session,Logger logger) {
		String[] properties = { "text", "tableData", "jcr:title", "title", "alt", "jcr:description" };
		try {
			if (null != cnode) {
				if (!cnode.hasProperty("isEncoded")) {
					for (String prop : properties) {
						if (cnode.hasProperty(prop)) {
							String data = cnode.getProperty(prop).getString();
							byte[] bytesEncoded = Base64.encodeBase64(data.getBytes());
							String encodedString = new String(bytesEncoded);
							cnode.setProperty(prop, encodedString);
						}
					}
					cnode.setProperty("isEncoded", true);
					session.save();
				}
				javax.jcr.NodeIterator nodeIt = cnode.getNodes();
				while (nodeIt.hasNext()) {
					javax.jcr.Node cn = nodeIt.nextNode();
					encodeAccordionContent(cn, session,logger);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	void decodeAccordionContent(Node cnode, Session session,Logger logger) {
		String[] properties = { "text", "tableData", "jcr:title", "title", "alt", "jcr:description" };
		try {
			if (null != cnode) {
				if (cnode.hasProperty("isEncoded")) {
					for (String prop : properties) {
						if (cnode.hasProperty(prop)) {
							String data = cnode.getProperty(prop).getString();
							byte[] valueDecoded = Base64.decodeBase64(data);
							String decodedString = new String(valueDecoded);
							cnode.setProperty(prop, decodedString);
						}
					}
					cnode.getProperty("isEncoded").remove();
					session.save();
				}
				javax.jcr.NodeIterator nodeIt = cnode.getNodes();
				while (nodeIt.hasNext()) {
					javax.jcr.Node cn = nodeIt.nextNode();
					decodeAccordionContent(cn, session,logger);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}%>
