<%@page session="false"
          import="java.text.Collator,
                  java.util.Collections,
                  java.util.Comparator,
                  java.util.Iterator,
                  java.util.List,
                  javax.servlet.jsp.JspWriter,
                  org.apache.commons.collections.IteratorUtils,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.adobe.granite.ui.components.Tag,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.xss.XSSAPI,
                  com.day.cq.i18n.I18n" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>                  
<cq:defineObjects />
<cq:includeClientLib categories="apps.girlscouts-vtk, apps.girlscouts-vtk.components.iconpicker" />
<%
	final ComponentHelper cmp = new ComponentHelper(pageContext);
	final I18n i18n = cmp.getI18n();
	final XSSAPI xss = cmp.getXss();
    Config cfg = cmp.getConfig();
    Iterator<Resource> itemIterator = cmp.getItemDataSource().iterator();  
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));
    attrs.addOther("collision", "none");
    attrs.add("data-init", "graphiciconselect");
    attrs.addOthers(cfg.getProperties(), "id", "class", "rel", "title", "name", "multiple", "disabled", "required", "renderReadOnly", "fieldLabel", "fieldDescription", "emptyText", "ignoreData", "translateOptions", "ordered");
    AttrBuilder selectAttrs = new AttrBuilder(request, xss);
    selectAttrs.add("name", cfg.get("name", String.class));
    selectAttrs.addMultiple(cfg.get("multiple", false));
    selectAttrs.addDisabled(cfg.get("disabled", false));
    AttrBuilder selectListAttrs = new AttrBuilder(request, xss);
    selectListAttrs.addOther("collision-adjustment", cfg.get("collisionAdjustment", String.class));
%>
<div class="coral-Form-fieldwrapper">
	<coral-select class="icon-picker" <%= selectAttrs.build() %>>
	<% 
		for (Iterator<Resource> items = itemIterator; items.hasNext();) {
			try{
				printOption(out, items.next(), cmp);
			}catch(Exception e){
				
			}
	   	}
	%>
	</coral-select>
</div>
<%!
    private void printOption(JspWriter out, Resource option, ComponentHelper cmp) throws Exception {
        final I18n i18n = cmp.getI18n();
        final XSSAPI xss = cmp.getXss();
        Config optionCfg = new Config(option);
        String value = cmp.getExpressionHelper().getString(optionCfg.get("value", String.class)); 
        AttrBuilder opAttrs = new AttrBuilder(null, cmp.getXss());
        opAttrs.add("id", optionCfg.get("id", String.class));
        opAttrs.addClass(optionCfg.get("class", String.class));
        opAttrs.addRel(optionCfg.get("rel", String.class));
        opAttrs.add("title", i18n.getVar(optionCfg.get("title", String.class)));
        opAttrs.add("value", value);
        opAttrs.addDisabled(optionCfg.get("disabled", false));
        opAttrs.addOthers(optionCfg.getProperties(), "id", "class", "rel", "title", "value", "text", "disabled", "selected", "group");
        // otherwise, render the <option>
        opAttrs.addSelected(cmp.getValue().isSelected(value, optionCfg.get("selected", false)));
        out.println("<coral-select-item " + opAttrs.build() + "><label style=\"font-size: 30px;\" class=\"icon-picker-icon "+value+"\"></label>&nbsp;&nbsp;" + xss.encodeForHTML(getOptionText(option, cmp)) + "</coral-select-item>");
    }

    private String getOptionText(Resource option, ComponentHelper cmp) {
        Config optionCfg = new Config(option);
        String text = optionCfg.get("text", "");        
        if (cmp.getConfig().get("translateOptions", true)) {
            text = cmp.getI18n().getVar(text);
        }        
        return text;
    }
%>