<%@ page
	import="java.lang.Exception,
	java.util.Optional,
	org.girlscouts.gsusa.badges.dto.BadgeDTO"%>
<%@ page import="java.util.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<cq:defineObjects />

<%@ include file="pdf-templates/pdf-template-wrapper.jsp" %>
<%!
class BadgeComparator implements Comparator<Resource>{

	public int compare(Resource badge1, Resource badge2){
		try{
			if(badge1 != null && badge2 != null){
				ValueMap b1ValMap = badge1.getChild("jcr:content/metadata").getValueMap();
				ValueMap b2ValMap = badge2.getChild("jcr:content/metadata").getValueMap();
				String b1Name = b1ValMap.get("dc:title", String.class);
				String b2Name = b2ValMap.get("dc:title", String.class);
				return b1Name.compareTo(b2Name);
			}
		}catch (Exception e){
			System.err.println("Error occured while comparing "+badge1+" and "+badge2);
			e.printStackTrace();
		}
		return 0;
	}
}
%>
<%
	/************************** Badge List Component ************************
	** This components lists badges under badges directory
	**
	*************************************************************************/

	String[] paths = properties.get("badgePaths", String[].class);
	if(paths != null && paths.length > 0){
		StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"badge-grid\" style=\"display: none;\">");
		int badgeCount = 0;
		for(String path : paths){
			if(path != null && path.trim().length() > 0){
				try{
					Resource content = resourceResolver.getResource(path);
					if(content != null && content.hasChildren()){
						Iterable<Resource> badges = content.getChildren();
						List<Resource> sortedBadgeList = new ArrayList<Resource>();
						for (Resource badge : badges) {
							if(badge.isResourceType("dam:Asset")){
								sortedBadgeList.add(badge);
							}
						}
						Collections.sort(sortedBadgeList, new BadgeComparator());
						for (Resource badge: sortedBadgeList){
							try{
								badgeCount++;
								String regImageSrc = badge.getPath();
								String smallImageSrc = badge.getPath();
								Asset asset = badge.adaptTo(Asset.class);
								regImageSrc = imageTransformer.getRendition("cq5dam.thumbnail.319.319");
								smallImageSrc = regImageSrc;
								Rendition regRendition = asset.getRendition("cq5dam.thumbnail.319.319.png");
// 								if(regRendition != null && regRendition.getPath() != null){
// 									regImageSrc = regRendition.getPath();
// 								}
							    Resource smallRendition = regRendition; //asset.getRendition("cq5dam.thumbnail.140.100.png");
// 							    if(smallRendition != null && smallRendition.getPath() != null){
// 							    	smallImageSrc = smallRendition.getPath();
// 								}
								Resource badgeMetadata = badge.getChild("jcr:content/metadata");
								ValueMap props = badgeMetadata.adaptTo(ValueMap.class);
								StringBuilder filterClassBuilder = new StringBuilder();
								String[] tags = props.get("cq:tags",String[].class);
								String title = props.get("dc:title",String.class);
								String description = props.get("dc:description",String.class);
								String badgeCTA = Optional.ofNullable(props.get("badgeCTA",String.class)).orElse("GET THIS BADGE");
								String restrictions = props.get("adobe_dam:restrictions",String.class);
								String imageSrc = badge.getPath();
								String modalId = "modal-"+badgeCount;
								if(tags != null && tags.length>0){
									filterClassBuilder.append(" filter=\"");
									for(String tagString : tags){
										String[] tagsArr = tagString.toLowerCase().split("/");
										if(tagsArr.length>3){
											for(int i=3;i < tagsArr.length;i++){
												filterClassBuilder.append(tagsArr[i].replace(" ",""));
												filterClassBuilder.append(" ");
											}
										}
									}
									filterClassBuilder.append("\"");
								}
								sb.append("<div class=\"badge-block\" "+filterClassBuilder.toString()+" data-badge-info='" + new BadgeDTO(badge, props).toJson() + "'>");
									sb.append("<div class=\"badge-content\">");
	                                    sb.append("<div class=\"badge-body\">");
	                                        sb.append("<label class=\"badge-image-wrapper\" for=\""+modalId+"\">");
	                                            sb.append("<div class=\"badge-image-body\">");
	                                                sb.append("<div class=\"badge-image-inner\">");
	                                                    sb.append("<img class=\"badge-image\" alt=\""+title+"\" src=\"" + regImageSrc + "\" />");
	                                                sb.append("</div>");
	                                            sb.append("</div>");
	                                        sb.append("</label>");
	                                    sb.append("</div>");
	                                    sb.append("<div class=\"badge-title-wrapper\">");
	                                        sb.append("<div class=\"badge-title-body\">");
	                                            sb.append("<label class=\"badge-title\" for=\""+modalId+"\">"+title+"</label>");
	                                        sb.append("</div>");
	                                    sb.append("</div>");
									sb.append("</div>");
									sb.append("<div class=\"modal\">");
										sb.append("<input class=\"modal-open\" id=\""+modalId+"\" type=\"checkbox\" hidden>");
										sb.append("<div class=\"modal-wrap\" aria-hidden=\"true\" role=\"dialog\">");
											sb.append("<label class=\"modal-overlay\" for=\""+modalId+"\"></label>");
											sb.append("<div class=\"modal-dialog\">");
												sb.append("<div class=\"modal-body\">");
													sb.append("<label class=\"btn-close\" for=\""+modalId+"\" aria-hidden=\"true\">&times;</label>");
	                                                sb.append("<div class=\"header\">");
	                                                    sb.append("<img class=\"badge-image\" alt=\""+title+"\" src=\"" + smallImageSrc + "\" />");
	                                                    sb.append("<p class=\"title\">"+title+"</p>");
	                                                sb.append("</div>");
	                                                sb.append("<div class=\"description-wrapper\">");
													    sb.append("<div class=\"description\">"+restrictions+"</div>");
	                                                sb.append("</div>");
													sb.append("<div class=\"btn-get-wrapper\">");
													if(description != null && description.trim().length() > 0){
														sb.append("<a class=\"btn-get\" href=\""+description+"\" target=\"_blank\">" + badgeCTA + "</a>");
													}
													sb.append("</div>");
												sb.append("</div>");
											sb.append("</div>");
										sb.append("</div>");
									sb.append("</div>");
								sb.append("</div>");
							}catch(Exception e){
								System.err.println("Error occurred while rendering badge at "+badge.getPath());
								e.printStackTrace();
							}
						}
					}
				}catch(Exception e){
					System.err.println("Error occurred while rendering badge images at "+path);
					e.printStackTrace();
				}
			}
		}
		sb.append("</div>");
		out.print(String.valueOf(sb));
	}else{
		out.print("Please set at least one valid path to badges.");
	}
%>
<cq:includeClientLib categories="apps.gsusa.components.badge-explorer"/>
