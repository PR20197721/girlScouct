<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
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
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.Iterator,
                  javax.servlet.jsp.JspWriter,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.adobe.granite.ui.components.Tag" %><%    

    Config cfg = cmp.getConfig();
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));

    attrs.addClass("coral-Select");
    attrs.addClass("tag-languages-select");
    attrs.add("data-init", "select");

    AttrBuilder buttonAttrs = new AttrBuilder(request, xssAPI);
    buttonAttrs.add("type", "button");
    buttonAttrs.addDisabled(cfg.get("disabled", false));
    buttonAttrs.addClass("coral-Select-button coral-MinimalButton tags-add-language-button");


    AttrBuilder selectAttrs = new AttrBuilder(request, xssAPI);
    selectAttrs.add("name", cfg.get("name", String.class));
    selectAttrs.addMultiple(cfg.get("multiple", true));
    selectAttrs.addDisabled(cfg.get("disabled", false));
    selectAttrs.addClass("coral-Select-select");

    AttrBuilder selectListAttrs = new AttrBuilder(request, xssAPI);
    selectListAttrs.addOther("collision-adjustment", cfg.get("collisionAdjustment", "none"));

    // TODO check if hardcoding can be avoided 
    Resource tagBaseResource = resourceResolver.resolve("/etc/tags");
    ValueMap tagBaseVM = tagBaseResource.adaptTo(ValueMap.class);
    Object langObj = tagBaseVM.get("languages");
    String languagesRoot = "/libs/wcm/core/resources/languages/";
    String[] languages = null;

    if (langObj != null) {
        languages = (String[]) langObj;
    }

%><div class="coral-Form-fieldlabel"><%= i18n.get("Languages") %></div>
<span <%= attrs.build() %>>
    <button <%= buttonAttrs.build() %>>
        <span class="coral-Select-button-text"><%= outVar(xssAPI, i18n, cfg.get("emptyText", "")) %></span>
    </button>
    <select <%= selectAttrs.build() %>><%

        for (int i = 0; i < languages.length; i++) {            
            String lang = languages[i].replace("-", "_").toLowerCase();
            Resource langResource = resourceResolver.resolve(languagesRoot + lang);
            ValueMap langResVM = langResource.adaptTo(ValueMap.class);            
            String langTitle = langResVM.get("language", String.class);
            if (langTitle != null) {
                langTitle = i18n.getVar(langTitle);
            }
            String country = langResVM.get("country", String.class);
            if (country != null && !country.equals("*")) {
                langTitle += " (" + i18n.getVar(country) + ")";
            }
            printOption(out, langTitle, languages[i], cmp);
        }
    %></select>
         <ul class="coral-SelectList" <%= selectListAttrs.build() %>></ul>
</span>
<div id="tag-selected-languages"></div>
  <%!

    private void printOption(JspWriter out, String langTitle, String propName, ComponentHelper cmp) throws Exception {
        I18n i18n = cmp.getI18n();
        XSSAPI xss = cmp.getXss();

        AttrBuilder opAttrs = new AttrBuilder(null, cmp.getXss());
        opAttrs.add("title", langTitle);
        opAttrs.add("value", propName);
        opAttrs.addClass(propName);
        out.println("<option " + opAttrs.build() + ">" + xss.encodeForHTML(langTitle) + "</option>");
    }        
%>