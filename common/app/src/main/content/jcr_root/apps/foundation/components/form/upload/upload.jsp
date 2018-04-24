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
%><%@ page import="com.day.cq.wcm.foundation.forms.FormsHelper,
        com.day.cq.wcm.foundation.forms.LayoutHelper,
        java.util.Locale,
		java.util.ResourceBundle" %><%
    final Locale pageLocale = currentPage.getLanguage(true);
    final ResourceBundle bundle = slingRequest.getResourceBundle(pageLocale);
    final String name = FormsHelper.getParameterName(resource);
    final String id = FormsHelper.getFieldId(slingRequest, resource);
    final boolean required = FormsHelper.isRequired(resource);
    final boolean hideTitle = properties.get("hideTitle", false);
    final String width = properties.get("width", "");
    String customValidationMessage = properties.get("requiredMessage", String.class);

    final String title = FormsHelper.getTitle(resource, bundle.getString("Upload"));
    %><div class="form_row">
        <% LayoutHelper.printTitle(id, title, required, hideTitle, out); %>
        <div class="form_rightcol"><input id="<%=id %>" <%
                                    %>class="<%= FormsHelper.getCss(properties, "form_field form_field_file") %> gs_file_upload" <%
                                    %>name="<%= xssAPI.encodeForHTMLAttr(name) %>" <%
                                    %>type="file" <%
                                    %>size="37" <%
           					  	    if(customValidationMessage != null && !"".equals(customValidationMessage)){
           					  	    	%>	
           					  	    		data-custom-validation-message="<%=customValidationMessage %>"
           					  	    	<%
           					  	    }
									if(required){
        							%>required="<%=required%>" <%
                                    }
                                    if (width.length() > 0) {
                                        %>style="width:<%= xssAPI.getValidInteger(width, 100) %>px;"<%
                                    }
        %>></div>
        
		<%--
			TODO@Matt: Move this to a client lib after the demo is solid.
		 --%>
		<script>
			$(function(){
		
			    $('.gs_file_upload[required="true"]').closest('form').find('input[type="submit"]').off('click.checkfile').on('click.checkfile', function(evt){
			        
					var currentForm = $(this).closest('form');
					currentForm.off('submit.checkfile');
					var fileInputs = currentForm.find('.gs_file_upload[required="true"]');
					var returner = true;
			        for(var i = 0; i < fileInputs.length; i++){
			            if(fileInputs[i].setCustomValidity != undefined){
			                fileInputs[i].setCustomValidity('');
			            }
			            if(fileInputs[i].files.length == 0){
			                returner = false;
			                
			                var input = $(fileInputs[i]);
			                var validityMessage = $(input).data('customValidationMessage');
			                if(!validityMessage || !validityMessage.length){
			                    validityMessage = 'Please select a file';
			                }
			                
			                if(input[0].setCustomValidity !== undefined){
			                		evt.preventDefault();
			                	
			                    var errorMsg = $('<div>').css({
			                        position: 'absolute',
			                        top: input.position().top + input.outerHeight(true),
			                        left: input.position().left,
			                        backgroundColor: '#fff3a5',
			                        display: 'none',
			                        padding: 10,
			                        borderRadius: 5,
			                        border: '1px solid #777'
			                    })
			                    .text(validityMessage)
			                    .appendTo(input.parent())
			                    .fadeIn('slow');
			                    
			                    window.setTimeout(function(){
			                        errorMsg.fadeOut('slow').promise().then(function(){
			                            $(this).remove();
			                        });
			                    }, 3000);
			                }else{
			                    input[0].setCustomValidity(validityMessage);
			                    return true;
			                }
			            }
			        }
			        return returner;
			    });
			});
		</script>
		        
    </div><%
    LayoutHelper.printDescription(FormsHelper.getDescription(resource, ""), out);
    LayoutHelper.printErrors(slingRequest, name, hideTitle, out);
%>