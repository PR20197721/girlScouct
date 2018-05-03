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
					org.apache.sling.api.request.RequestParameterMap" %><%

    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());
    Field field = new Field(cfg);

    final String[] values = vm.get("value", String[].class);
    
    final String name = cfg.get("name", String.class);
	String requestURL = request.getRequestURL().toString();
	String requestItem = request.getParameter("item");
	String truncatedPath = requestItem != null ? requestItem : requestURL.replaceAll(".*_cq_dialog\\.html","");
	String customRootPath = "/";
	String[] contentItems = truncatedPath.split("/");
	if(contentItems.length > 2){
        customRootPath = "/" + contentItems[1] + "/" + contentItems[2];
	}
    boolean mixed = field.isMixed(cmp.getValue());

    String predicate = cfg.get("predicate", "hierarchyNotFile"); // 'folder', 'hierarchy', 'hierarchyNotFile' or 'nosystem'
    String rootPath = cmp.getExpressionHelper().getString(cfg.get("rootPath", customRootPath));
    String defaultPickerSrc = "/libs/wcm/core/content/common/pathbrowser/column.html" + Text.escapePath(rootPath) + "?predicate=" + Text.escape(predicate);
    
    String crumbRoot = cfg.get("crumbRoot", String.class);
    if (crumbRoot == null) {
        Resource rootResource = resourceResolver.getResource(rootPath);
        if (rootResource != null) {
            crumbRoot = rootResource.getValueMap().get("jcr:title", rootResource.getName());
        }

        if (StringUtils.isEmpty(crumbRoot)) {
            crumbRoot = i18n.get("Home");
        }
    }
    
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));
    
    attrs.addClass("coral-PathBrowser");
    attrs.add("data-init", "pathbrowser");
    attrs.add("data-root-path", rootPath);
    attrs.add("data-option-loader", cfg.get("optionLoader", "granite.ui.pathBrowser.pages." + predicate));
    attrs.add("data-option-loader-root", cfg.get("optionLoaderRoot", String.class));
    attrs.add("data-option-value-reader", cfg.get("optionValueReader", String.class));
    attrs.add("data-option-title-reader", cfg.get("optionTitleReader", String.class));
    attrs.add("data-option-renderer", cfg.get("optionRenderer", String.class));
	attrs.add("data-autocomplete-callback", cfg.get("autocompleteCallback", String.class));
    attrs.addHref("data-picker-src", cfg.get("pickerSrc", defaultPickerSrc));
    if (cfg.get("pickerTitle", String.class) != null) {
        attrs.add("data-picker-title", i18n.getVar(cfg.get("pickerTitle", String.class)));
    } else {
        attrs.add("data-picker-title", i18n.get("Select Path"));
    }
    attrs.add("data-picker-value-key", cfg.get("pickerValueKey", String.class));
    attrs.add("data-picker-id-key", cfg.get("pickerIdKey", String.class));
    attrs.add("data-crumb-root", crumbRoot);
    attrs.add("data-picker-multiselect", cfg.get("pickerMultiselect", false));
    attrs.add("data-root-path-valid-selection", cfg.get("rootPathValidSelection", true));
    
    if (cfg.get("disabled", false)) {
        attrs.add("data-disabled", true);
    }

    if (mixed) {
        attrs.addClass("foundation-field-mixed");
    }

    attrs.addOthers(cfg.getProperties(),
            "id", "class", "rel", "title",
            "name", "value", "emptyText", "disabled", "required", "validation",
            "predicate", "rootPath", "optionLoader", "optionLoaderRoot", "optionValueReader", "optionTitleReader", "optionRenderer", "autocompleteCallback",
            "crumbRoot", "pickerSrc", "pickerTitle", "pickerValueKey", "pickerIdKey", "pickerMultiselect", "rootPathValidSelection",
            "icon", "fieldLabel", "fieldDescription", "renderReadOnly", "ignoreData");

    AttrBuilder inputAttrs = new AttrBuilder(request, xssAPI);
    inputAttrs.addClass("coral-InputGroup-input coral-Textfield");
    inputAttrs.addClass("js-coral-pathbrowser-input");
    inputAttrs.add("type", "text");
    inputAttrs.add("name", cfg.get("name", String.class));
    inputAttrs.add("autocomplete", "off");

    if (mixed) {
        inputAttrs.add("placeholder", i18n.get("<Mixed Entries>"));
    } else {
        inputAttrs.add("value", vm.get("value", String.class));
        inputAttrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
    }

    if (cfg.get("required", false)) {
        inputAttrs.add("aria-required", true);
    }
    
    String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
    inputAttrs.add("data-validation", validation);

    AttrBuilder browseBtnAttrs = new AttrBuilder(request, xssAPI);
    browseBtnAttrs.addClass("coral-Button coral-Button--square js-coral-pathbrowser-button");
    browseBtnAttrs.add("type", "button");
    browseBtnAttrs.add("title", i18n.get("Browse") );
    if(cfg.get("hideBrowseBtn", false)){
        browseBtnAttrs.add("hidden","");
    }

%><span <%= attrs.build() %>>
    <span class="coral-InputGroup coral-InputGroup--block">
        <input <%= inputAttrs.build() %>>
        <span class="coral-InputGroup-button">
            <button <%= browseBtnAttrs.build() %>>
                <i class="coral-Icon coral-Icon--sizeS <%= xssAPI.encodeForHTMLAttr(cmp.getIconClass(cfg.get("icon", "icon-folderSearch"))) %>"></i>
            </button>
        </span>
    </span>
</span>