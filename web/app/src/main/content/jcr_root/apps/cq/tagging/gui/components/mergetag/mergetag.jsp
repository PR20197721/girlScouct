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
--%><%
%><%@page import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.day.cq.tagging.Tag,
                  org.apache.sling.api.resource.ValueMap,
                  java.text.Format,
                  java.text.SimpleDateFormat,
                  java.util.Calendar,
                  java.util.Date, com.adobe.granite.ui.components.ds.ValueMapResource, java.util.HashMap, org.apache.sling.api.wrappers.ValueMapDecorator, org.apache.jackrabbit.util.Text"
          session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%

    Config cfg = cmp.getConfig();
    String item = slingRequest.getRequestPathInfo().getSuffix();

    Resource itemRes = item != null ? resourceResolver.getResource(item) : null;
    if (itemRes == null) {
        %><h2 class="coral-Heading coral-Heading--2 u-cq-wizardError"><%= i18n.get("Item not found.") %></h2><%
        return;
    }

    Tag tag = itemRes.adaptTo(Tag.class);
    String title = tag.getTitle();
    String name = tag.getName();
    String path = tag.getPath();

    String rootPath = "/etc/tags";

    Format formatter = new SimpleDateFormat(i18n.get("MM/dd/yyyy","Java date format for a date (http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html)"));

    long tagLastModification = tag.getLastModified();
    ValueMap vm = itemRes.adaptTo(ValueMap.class);
    if (tagLastModification == 0) {
        Calendar created = vm.get("jcr:created", Calendar.class);
        tagLastModification = (null != created) ? created.getTimeInMillis() : 0;
    }
    String lastModified = i18n.getVar(formatter.format(tagLastModification));

    String lastModifiedBy = tag.getLastModifiedBy();

    if (lastModifiedBy == null) {
        lastModifiedBy = vm.get("jcr:createdBy", String.class);
    }

    String redirectPath = cfg.get("redirect", String.class);

    AttrBuilder attrs = cmp.consumeTag().getAttrs();
    attrs.add("title", i18n.get("Select Parent Path"));
    attrs.addOther("basepath", rootPath);
    attrs.addClass("cq-TagsPickerField");

    String templatePickerSrc = "/libs/wcm/core/content/common/tagbrowser/tagbrowsercolumn.html" + Text.escapePath(rootPath);

    ValueMap pathBrowserProperties = new ValueMapDecorator(new HashMap<String, Object>());
    pathBrowserProperties.put("rootPath", rootPath);
    pathBrowserProperties.put("name", "dest");
    pathBrowserProperties.put("value", rootPath);
    pathBrowserProperties.put("required", true);
    pathBrowserProperties.put("pickerSrc", templatePickerSrc);
    pathBrowserProperties.put("pickerMultiselect", false);
    pathBrowserProperties.put("pickerTitle", i18n.get("Select Parent Path"));
    pathBrowserProperties.put("property-path", xssAPI.encodeForHTMLAttr(cfg.get("name", "cq:tags")));
    pathBrowserProperties.put("icon", cfg.get("icon", "icon-browse"));
    pathBrowserProperties.put("browserPath", xssAPI.encodeForHTMLAttr(request.getContextPath() + resource.getPath()));
    ValueMapResource pathBrowser = new ValueMapResource(resourceResolver, resource.getPath(), "granite/ui/components/foundation/form/pathbrowser", pathBrowserProperties);

%>
<script type="text/javascript">
    $(document).on("foundation-contentloaded", function(e) {
        $(".mergetag-wizard-title").val("<%=xssAPI.encodeForHTML(title) %>");
     });

</script>

<ui:includeClientLib categories="cq.tagging.touch.mergetag" />
<div id="mergetagsettings" data-redirect="<%= xssAPI.getValidHref(redirectPath) %>">
<div class="foundation-content-path hidden" data-foundation-content-path="<%= xssAPI.encodeForHTMLAttr(item) %>"></div>

<section class="rename-container coral-FixedColumn">    

     <div class="coral-FixedColumn-column">
        <section>
            <section class="coral-Form-fieldset">
                <input class="coral-Form-field coral-Textfield" type="hidden" name="cmd" value="mergeTag">
                <input class="coral-Form-field coral-Textfield" name="path" type="hidden" value="<%= xssAPI.encodeForHTMLAttr(path) %>">
                <h3 class="coral-Form-fieldset-legend"><%= i18n.get("Merge {0}", null, xssAPI.filterHTML(title)) %></h3>
                <label class="coral-Form-fieldlabel "><%= i18n.get("Path") %></label>

                <input class="coral-Form-field coral-Textfield mergetag-original-path" name="path" type="text" value="<%= xssAPI.encodeForHTMLAttr(path) %>" disabled>
                <label class="coral-Form-fieldlabel "><%= i18n.get("Merge into") %></label>

                <div <%= attrs.build() %>>
                    <sling:include resource="<%= pathBrowser %>"/>
                </div>

            </section>
             <section class="coral-Form-fieldset">
                 <h3 class="coral-Form-fieldset-legend"><%= i18n.get("Information") %></h3>
                 <div class="coral-FixedColumn info-details">
                     <div class="coral-FixedColumn-column">
                          <label class="coral-Form-fieldlabel "><%= i18n.get("LAST MODIFIED ON") %></label>
                          <label class="coral-Form-fieldlabel "><%= i18n.get("LAST MODIFIED BY") %></label>
                     </div>
                     <div class="coral-FixedColumn-column">
                         <label class="coral-Form-fieldlabel "><%= lastModified %></label>
                         <label class="coral-Form-fieldlabel "><%= lastModifiedBy %></label>
                     </div>
                 </div>
            </section>
        </section>
    </div>
</section>
</div>
