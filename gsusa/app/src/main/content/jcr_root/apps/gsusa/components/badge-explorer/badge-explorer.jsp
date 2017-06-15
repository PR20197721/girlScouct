<%@ page
	import="java.lang.Exception,
	java.util.Date,
	java.util.List,
	java.util.ArrayList,
	java.util.Comparator,
	java.util.Collections"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />
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
<style>
    .hide {
        display: none !important;
    }    
    /*
    *
    * https://www.felipefialho.com/css-components/#component-modal 
    *
    */
    .modal {
        position: absolute;
    }

    .modal .btn-close,
    .modal .badge-image,
    .modal .btn-get {
        -webkit-user-select: none;
        /* Chrome, Opera, Safari */
        -moz-user-select: none;
        /* Firefox 2+ */
        -ms-user-select: none;
        /* IE 10+ */
        user-select: none;
        /* Standard syntax */
    }

    .modal .btn-close {
        color: #bababa;
        cursor: pointer;
        font-size: 20px;
        position: absolute;
        top: 2px;
        right: 10px;
        text-decoration: none;
    }

    .modal .btn-close:hover,
    .modal .btn-close:focus {
        color: #999;
    }

    .modal-wrap:before {
        content: '';
        display: none;
        background: rgba(0, 0, 0, 0.6);
        position: fixed;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        z-index: 101;
    }

    .modal-overlay {
        display: none;
        position: fixed;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        z-index: 102;
    }

    .modal-open:checked ~ .modal-wrap:before,
    .modal-open:checked ~ .modal-wrap .modal-overlay {
        display: block;
    }

    .modal-open:checked ~ .modal-wrap .modal-dialog {
        transform: translate(-50%, -50%);
        top: 48%;
    }

    .modal-dialog {
        display: table;
        background-color: #dbdcde;
        border: #555 solid 1px;
        position: fixed;
        left: 50%;
        top: -100%;
        transform: translate(-50%, -150%);
        transition: transform .6s ease-in-out;
        max-width: 466px;
        width: 100%;
        min-height: 400px;
        height: 1px; /* For Firefox */
        z-index: 103;
        padding: 10px;
    }
    
    @media only screen and (max-width: 767px) {
        .modal-dialog {
            display: none;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            padding: 0;
            transform: none;
            transition: none;
            border: none;
            height: 100%;
            max-width: 100%;
            min-height: 1px;
            height: auto;
        }
        .modal-open:checked ~ .modal-wrap .modal-dialog {
            transform: none;
            display: block;
            top: 0;
        }
    }

    .modal-body {
        display: table-cell;
        position: relative;
        background-color: #f7f8f9;
        width: 100%;
        height: 100%;
        padding: 28px 23px 50px 23px; /* 50px = wrapper 30px + btn-get 20px */
    }
    
    .modal-body * {
        font-size: 14px;
        line-height: normal;
    }

    .modal-body p,
    .modal-body ol,
    .modal-body ul {
        margin-bottom: 14px; /* Match font size */
        color: #222;
    }
    
    .modal .title {
        font-weight: 500;
        font-size: 16px;
    }

    .modal .badge-image {
        height: auto;
        max-width: 120px;
        float: right;
        padding: 0;
        margin-right: -5px;
    }
    
    @media only screen and (max-width: 767px) {
        .modal-body {
            display: table;
            padding-bottom: 65px; /* 65px = wrapper 30px + btn-get 20px + margin 15px */
        }
        
        .modal .header {
            display: table-row;
        }
        
        .modal .description-wrapper {
            display: table-row;
            height: 100%;
        }
        
        .modal .description {
            display: block;
            display: -moz-groupbox; /* For Firefox Mobile */
            width: 100%;
            height: 100%;
            overflow-y: auto;
        }

        .modal .badge-image {
            float: none;
            margin: 0 auto;
            display: block;
            margin-bottom: 20px;
        }
    }

    .modal .btn-get-wrapper {
        position: absolute;
        left: 0;
        bottom: 20px;
        width: 100%;
    }

    .modal .btn-get {
        display: block;
        width: 125px;
        line-height: 30px;
        text-align: center;
        margin: 0 auto;
        background-color: #00AE58;
        color: #fefefe;
        text-decoration: none;
    }
    
    /*
    *
    * Badge Grid
    *
    */

    .badge-grid {
        margin: -1.3%; 
        text-align: left;
        margin-bottom: 50px;
    }

    .badge-block {
        position: relative;
        margin: 1.3%; /* 10px at 770px container width */
        display: inline-block;
        width: 164px;
        background-color: #dbdcde;
        vertical-align: middle; /* To remove inline-block bottom spacing */
        padding: 10px;
    }

    .badge-content {
        width: 100%;
        background-color: #f8f8f8;
        text-align: center;
    }
    
    .badge-body {
        width: 100%;
        position: relative;
    }
    
    .badge-body:before {
        content: '';
        display: block;
        padding-top: 100%; /* 1:1 aspect ratio */
    }
    
    .badge-image-wrapper {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }
    
    .badge-image-body {
        display: table;
        width: 100%;
        height: 100%;
    }
    
    .badge-image-inner {
        display: table-cell;
        vertical-align: middle;
        width: 100%;
        height: 100%;
    }

    .badge-image {
        padding: 5px;
    }
    
    .badge-title-wrapper {
        display: table;
        width: 100%;
        min-height: 38px;
        height: 1px; /* For Firefox */
        cursor: pointer;
        background-color: white;
        border-top: 2px solid #dbdcde;
    }
    
    .badge-title-body {
        display: table-cell;
        vertical-align: middle;
        width: 100%;
        padding: 0 5px;
    }

    .badge-title {
        font-size: 12px;
        font-weight: 600;
        display: block;
        color: #222;
        line-height: 1.2em;
    }
    
    /* 3 Column - Tablet */
    @media only screen and (min-width: 521px) and (max-width: 945px) {
        .badge-block {
            width: 30.7%;
        }
    }
    
    /* 2 Column - Mobile */
    @media only screen and (min-width: 0px) and (max-width: 520px) {
        .badge-grid {
            margin: -3.045%;
        }
        
        .badge-block {
            width: 43.9%;
            margin: 3.045%; /* 10px at 320px container with */
        }
    }
</style>
<%
	/************************** Badge List Component ************************
	** This components lists badges under badges directory 
	**
	*************************************************************************/

	String[] paths = properties.get("badgePaths", String[].class);
	if(paths != null && paths.length > 0){
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"badge-grid\">");
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
							badgeCount++;
							String regImageSrc = badge.getPath();
							String smallImageSrc = badge.getPath();
							Asset asset = badge.adaptTo(Asset.class);
							Rendition regRendition = asset.getRendition("cq5dam.thumbnail.319.319.png");
							if(regRendition != null && regRendition.getPath() != null){
								regImageSrc = regRendition.getPath();
							}
						    Resource smallRendition = regRendition; //asset.getRendition("cq5dam.thumbnail.140.100.png");
						    if(smallRendition != null && smallRendition.getPath() != null){
						    	smallImageSrc = smallRendition.getPath();
							}
							Resource badgeMetadata = badge.getChild("jcr:content/metadata");
							ValueMap props = badgeMetadata.adaptTo(ValueMap.class);
							StringBuilder filterClassBuilder = new StringBuilder();
							String[] tags = props.get("cq:tags",String[].class);
							String title = props.get("dc:title",String.class);
							String description = props.get("dc:description",String.class);
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
							sb.append("<div class=\"badge-block\" "+filterClassBuilder.toString()+">");
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
													sb.append("<a class=\"btn-get\" href=\""+description+"\" target=\"_blank\">GET THIS BADGE</a>");
												}
												sb.append("</div>");
											sb.append("</div>");
										sb.append("</div>");
									sb.append("</div>");
								sb.append("</div>");
							sb.append("</div>");
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

<script>
    $(document).ready(function () {
        var badges = $(".badge-block"),
            filterSets = {},
            filter,
            id,
            group,
            activeFilters = {},
            hideClass = "hide",
            groupClass = "group-",
            submenu = $(".submenu");

        $.fn.show = function () {
            return this.removeClass(hideClass);
        };
        $.fn.hide = function () {
            return this.addClass(hideClass);
        };
        $.fn.removeClassAll = function (name) {
            return this.removeClass(function (index, css) {
                return (css.match(new RegExp("(^|\\s)" + name + "\\S+", "g")) || []).join(' ');
            });
        };

        function hasActiveFilters() {
            return Object.keys(activeFilters).length;
        }

        $(".submenu label").each(function () {
            filter = $(this).html();
            id = $(this).attr("for");
            group = Number(id.split("-")[1]);
            filterSets[filter] = {
                badges: $(".badge-block[filter~='" + filter.replace(/\s+/g, '').toLowerCase() + "']"), // Remove all spaces, ~ is for whitespace separated selector
                tag: $(".tags label[for='" + id + "']"),
                dropdown: $("#dropdown-" + group),
                group: group
            };
        });

        /*$(".submenu label").on("click", function () {
            filter = $(this).html();
            filterSets[filter].dropdown.click(); // Close dropdown
        });
        
        $(document).on("click", function(event) {
            var target = $(event.target),
                dropdown = target.closest(".dropdown > li");
            if (!dropdown.length) {
                $(".dropdown > li > input[type='checkbox']").prop("checked", false);
            }
        });*/
        
        // Close dropdown if open on click
        $(document).on("click", function() {
            submenu.each(function () {
                if ($(this).height() > 1) {
                    $(this).siblings("input[type='checkbox']").prop("checked", false);
                }
            });
        });

        $(".submenu input[type='checkbox']").on("change", function () {
            var filter = $(this).siblings("label").html(),
                active = this.checked, // Check current active state of filter
                f,
                g,
                intersect = "";

            // Show/Hide badges
            if (active) {
                activeFilters[filter] = true;
                filterSets[filter].tag.show();
            } else {
                delete activeFilters[filter];
                filterSets[filter].tag.hide();
            }

            if (hasActiveFilters()) { // If there are other active filters
                for (f in activeFilters) {
                    g = groupClass + filterSets[f].group;
                    filterSets[f].badges.addClass(g);
                    if (intersect.indexOf(g) < 0) { // If the filter group isn't already in the selector string
                        intersect += "." + g; // No space before period for AND relationship, add comma with space after g for OR
                    }
                }
                badges.hide();
                $(intersect).show();
                badges.removeClassAll(groupClass);
            } else { // If there are no active filters
                badges.show();
            }

        });
    }());
</script>