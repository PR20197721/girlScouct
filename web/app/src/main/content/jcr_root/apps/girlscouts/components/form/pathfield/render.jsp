<%--
  ADOBE CONFIDENTIAL

  Copyright 2016 Adobe Systems Incorporated
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
%><%@include file="/libs/granite/ui/global.jsp" %>
<%

%><%@page session="false"
          import="org.apache.commons.lang.StringUtils,
                  org.apache.jackrabbit.util.Text,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag,
					org.apache.sling.api.request.RequestParameterMap" %><%--###
PathField
=========

.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/form/pathfield
   :supertype: /libs/granite/ui/components/coral/foundation/form/field
   
   A field that allows the user to enter path.
   
   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.
   
   It supports the concept of suggestion where the list of options are presented based on what the user types.
   It also supports the concept of picker where the user allows to pick the value in more advanced way (usually implemented as a dialog showing a tree view).
   
   **Usage Guidelines**
   
   In Sling, a resource (thus a path) is a first class citizen. So naturally many things, such as WCM Pages, DAM Assets, Commerce Products are represented as paths.
   This field can then be used for that purpose. However, this field is only meant for general purpose pathfield, even though the suggestion and picker can be configured.
   It is recommended to create a dedicated field for each resource type accordingly.
   e.g. It is better to create a dedicated field for the purpose of selecting CQ Pages, with the applicable configuration configured OOTB.
   This is to allow convenient usage of the said field without forcing the author to know about URL, which can be an implementation detail. 
   
   It has the following content structure:

   .. gnd:gnd::

      [granite:FormTextField] > granite:FormField
      
      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)
      
      /**
       * A hint to the user of what can be entered in the field.
       */
      - emptyText (String) i18n
      
      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)
      
      /**
       * Indicates if the field is mandatory to be filled.
       */
      - required (Boolean)
            
      /**
       * The name of the validator to be applied. E.g. ``foundation.jcr.name``.
       * See :doc:`validation </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/validation/index>` in Granite UI.
       */
      - validation (String) multiple
      
      /**
       * Indicates if the user is able to select multiple selections.
       */
      - multiple (Boolean)
      
      /**
       * Indicates if the user must only select from the list of given options.
       * If it is not forced, the user can enter arbitrary value.
       */
      - forceSelection (Boolean)
      
      /**
       * ``true`` to generate the `SlingPostServlet @Delete <http://sling.apache.org/documentation/bundles/manipulating-content-the-slingpostservlet-servlets-post.html#delete>`_ hidden input based on the field name.
       */
      - deleteHint (Boolean) = true
      
      /**
       * The URI Template that returns the suggestion markup.
       *
       * The default value is ``/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/suggestion{.offset,limit}.html?_charset_=utf-8&root=<rootPath>&filter=<filter>{&query}``.
       *
       * With the following variables resolved in the server:
       *
       * rootPath
       *    The value of ``rootPath`` property.
       *
       * filter
       *    The value of ``filter`` property.
       */
      - suggestionSrc (StringEL)
      
      /**
       * The URL that returns the picker markup.
       *
       * The default value is ``/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/picker.html?_charset_=utf-8&root=<rootPath>&filter=<filter>&selectionCount=<selectionCount>``.
       *
       * With the following variables resolved in the server:
       *
       * rootPath
       *    The value of ``rootPath`` property.
       *
       * filter
       *    The value of ``filter`` property.
       *
       * selectionCount
       *    If ``multiple`` property is ``true`` then ``multiple``; ``single`` otherwise.
       */
      - pickerSrc (StringEl) mandatory
      
      /**
       * The path of the root of the pathfield.
       */
      - rootPath (StringEL) = '/' mandatory
      
      /**
       * The filter applied to suggestion and picker.
       *
       * Valid values are:
       *
       * folder
       *    Shows only ``nt:folder`` nodes.
       *
       * hierarchy
       *    Shows only ``nt:hierarchyNode`` nodes.
       *
       * hierarchyNotFile
       *    Shows only ``nt:hierarchyNode`` nodes that are not ``nt:file``.
       *
       * nosystem
       *    Shows non-system nodes: ``!node.getName().startsWith("rep:") && !node.getName().equals("jcr:system")``.
       */
      - filter (String) = 'hierarchyNotFile' mandatory < 'folder', 'hierarchy', 'hierarchyNotFile', 'nosystem'
###--%><%

    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());
    Field field = new Field(cfg);
    
    ExpressionHelper ex = cmp.getExpressionHelper();
    
    final String[] values = vm.get("value", String[].class);
    
    final String name = cfg.get("name", String.class);
	String requestURL = request.getRequestURL().toString();
	String truncatedPath = requestURL.replaceAll(".*_cq_dialog\\.html","");
	String customRootPath = "/";
	String[] contentItems = truncatedPath.split("/");
	if(contentItems.length > 2){
        customRootPath = "/" + contentItems[1] + "/" + contentItems[2];
	}



final String rootPath = ex.getString(cfg.get("rootPath", customRootPath));
    final String filter = cfg.get("filter", "hierarchyNotFile");
    final boolean multiple = cfg.get("multiple", false);
    
    final String selectionCount = multiple ? "multiple" : "single";

    final String defaultPickerSrc = "/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/picker.html?_charset_=utf-8&root=" + Text.escape(rootPath) + "&filter=" + Text.escape(filter) + "&selectionCount=" + Text.escape(selectionCount);    
    final String pickerSrc = ex.getString(cfg.get("pickerSrc", defaultPickerSrc));
    
    final String defaultSuggestionSrc = "/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/suggestion{.offset,limit}.html?_charset_=utf-8&root=" + Text.escape(rootPath) + "&filter=" + Text.escape(filter) + "{&query}";
    final String suggestionSrc = ex.getString(cfg.get("suggestionSrc", defaultSuggestionSrc));
    
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.add("name", name);
    attrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
    attrs.addDisabled(cfg.get("disabled", false));
    attrs.addBoolean("multiple", multiple);
    attrs.addBoolean("required", cfg.get("required", false));
    attrs.addBoolean("forceselection", cfg.get("forceSelection", false));
    attrs.addHref("pickersrc", pickerSrc);
    
    if (multiple) {
        attrs.add("valuedisplaymode", "block");
    }
    
    attrs.add("data-foundation-validation", StringUtils.join(cfg.get("validation", new String[0]), " "));
    
    AttrBuilder suggestionAttrs = new AttrBuilder(request, xssAPI);
    suggestionAttrs.add("foundation-autocomplete-suggestion", "");
    suggestionAttrs.addClass("foundation-picker-buttonlist");
    suggestionAttrs.add("data-foundation-picker-buttonlist-src", request.getContextPath() + suggestionSrc);
    
    AttrBuilder valueAttrs = new AttrBuilder(request, xssAPI);
    valueAttrs.add("foundation-autocomplete-value", "");
    valueAttrs.add("name", name);

%><foundation-autocomplete <%= attrs %>>
    <coral-overlay <%= suggestionAttrs %>></coral-overlay>
    <coral-taglist <%= valueAttrs %>><%
        for (String value : values) {
            %><coral-tag value="<%= xssAPI.encodeForHTMLAttr(value) %>"><%= xssAPI.encodeForHTML(value) %></coral-tag><%
        }
    %></coral-taglist><%

    if (!StringUtils.isBlank(name)) {
        if (cfg.get("deleteHint", true)) {
            AttrBuilder deleteAttrs = new AttrBuilder(request, xssAPI);
            deleteAttrs.addClass("foundation-field-related");
            deleteAttrs.add("type", "hidden");
            deleteAttrs.add("name", name + "@Delete");
            
            %><input <%= deleteAttrs %>><%
        }
    }
%></foundation-autocomplete>