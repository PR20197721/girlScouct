<%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="org.apache.commons.lang.StringUtils,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %>
<%
    if (!cmp.getRenderCondition(resource, false).check()) {
        return;
    }
    
    Config cfg = cmp.getConfig();
    
    String value = cmp.getValue().val(cmp.getExpressionHelper().getString(cfg.get("value", "")));

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.add("type", "hidden");
    attrs.add("name", cfg.get("name", String.class));
    attrs.add("value", value);
    attrs.addDisabled(cfg.get("disabled", false));

%>
<input <%= attrs.build() %>>
<%
    AttrBuilder textFieldAttrs = new AttrBuilder(request, xssAPI);
	textFieldAttrs.add("type", "text");
	textFieldAttrs.add("name", cfg.get("name", String.class));
	textFieldAttrs.add("value", vm.get("value", String.class));
   	textFieldAttrs.addClass("coral-Textfield");
    
%><table><tr><td><input <%= attrs.build() %>></td><td><input <%= attrs.build() %>></td><td><input <%= attrs.build() %>></td></tr></table>