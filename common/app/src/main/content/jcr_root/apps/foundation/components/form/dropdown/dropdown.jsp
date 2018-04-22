<%@page session="false"%><%--
Copyright 1997-2010 Day Management AG
Barfuesserplatz 6, 4001 Basel, Switzerland
All Rights Reserved.

This software is the confidential and proprietary information of
Day Management AG, ("Confidential Information"). You shall not
disclose such Confidential Information and shall use it only in
accordance with the terms of the license agreement you entered into
with Day.

==============================================================================

Form 'element' component

Draws an element of a form

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="java.util.LinkedHashMap,
    java.util.List,
    java.util.Map,
	java.util.ArrayList,
    java.util.Collections,
    com.day.cq.wcm.foundation.forms.FormsHelper,
    com.day.cq.wcm.foundation.forms.LayoutHelper,
    com.day.cq.wcm.foundation.forms.ValidationInfo,
    java.util.Locale,
    java.util.ResourceBundle,
    com.day.cq.i18n.I18n" %>
    
<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
I18n i18n = new I18n(resourceBundle);

final String name = FormsHelper.getParameterName(resource);
final String id = FormsHelper.getFieldId(slingRequest, resource);
final boolean required = FormsHelper.isRequired(resource);
final boolean hideTitle = properties.get("hideTitle", false);
final String title = FormsHelper.getTitle(resource, i18n.get("Selection"));

List<String> values = FormsHelper.getValuesAsList(slingRequest, resource);


Map<String, String> displayValues = FormsHelper.getOptions(slingRequest, resource);
if (displayValues == null) {
    displayValues = new LinkedHashMap<String, String>();
    displayValues.put("item1", i18n.get("Item 1"));
    displayValues.put("item2", i18n.get("Item 2"));
    displayValues.put("item3", i18n.get("Item 3"));
}

final String classes = FormsHelper.getCss(properties, "form_field form_field_select");
final String multiSelect = FormsHelper.hasMultiSelection(resource) ? "multiple='multiple'" : "";
final String w = properties.get("width", "");
final String h = properties.get("height", "");
final String width = w.length() > 0 ? "style='width:" + w + "px;'" : "";
final String height = h.length() > 0 ? "style='height:" + h + "px;'" : "";
final String firstOption = displayValues.entrySet().iterator().next().getKey();
System.out.println("Dropdown Values type is + " + values.getClass().getName());
if(values.isEmpty()){
    values = new ArrayList<String>();
	values.add(firstOption);
}

%><div class="form_row"><% 
    LayoutHelper.printTitle(id, title, required, hideTitle, out); 
    %><div class="form_rightcol"><%
        %><select class="<%=classes%>" id="<%=id%>" name="<%=name%>" <%=multiSelect%> <%=width%> <%=height%>><%
            for (String key : displayValues.keySet()) {
                final String v = key;
                final String t = displayValues.get(key);
                //final String s = values.contains(v) ? "selected" : "";
                final String s = values.contains(v) ? "selected" : "";

                %><option value="<%=v%>" <%=s%>><%=t%></option><%
            }
        %></select>
    </div>
</div><%

LayoutHelper.printDescription(FormsHelper.getDescription(resource, ""), out);
LayoutHelper.printErrors(slingRequest, name, hideTitle, out);
%>
