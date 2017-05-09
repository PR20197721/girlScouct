<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode"%>

<%
boolean zip = properties.get("zip", false);
boolean state = properties.get("state", false);
boolean councilName = properties.get("council-name", false);
String path = properties.get("path","");
String councilCode = slingRequest.getParameter("council-code") != null ? slingRequest.getParameter("council-code") : "";

if (path.equals("") || (!zip && !state && !councilName) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %><p>**Please select at least one search type</p><% 
} else if (zip || state || councilName) {
    %><h3>Find Councils</h3>
    <ul class="block-grid"><%
        if (zip) {
            %><li>
                <form class="zipSearch" name="zipSearch">
                    <h6>By Zip Code</h6>
                    <p>Find the Girl Scout<br/> Council Serving Your Area</p>
                    <section>
                        <input required type="text" pattern="[0-9]*" name="zip" placeholder="Enter ZIP Code" />
                        <input type="submit" value="Go" class="button tiny" />
                    </section>
                </form>
            </li><%
        } 
        if (state) {
            %><li>
                <form class="stateSearch" name="stateSearch">
                    <h6>By State</h6>
                    <p>Find a Girl Scout<br/> Council by State</p>
                    <cq:include script="state-form.jsp" />
                </form>
            </li><%
        }
        if (councilName) {
            %><li>
				<form class="councilCodeSearch" name="councilCodeSearch">
                    <h6>By Council Name</h6>
					<p>Find a Girl Scout<br/> Council by State</p>
                    <section>
                        <select required name="council-code">
                            <option value="">Select a Council:</option>
                        </select>
                        <input type="submit" value="Go" class="button tiny"/>
                    </section>
				</form>
			</li>
        
			<script type="text/javascript">
                $(document).ready(function () {
                    $.get("/councilfinder/ajax_results.asp?short=yes", function(data) {
                        var request = "<%=councilCode%>",
                            json = JSON.parse(data),
                            codeSelect = $(".councilCodeSearch [name='council-code']")[0],
                            appendStr = "",
                            selected,
                            code,
                            name;
                        for (var i = 0; i < json.councils.length; i++) {
                            code = json.councils[i].councilCode;
                            name = json.councils[i].councilShortName;
                            selected = request == code ? "selected=\"selected\" " : "";
                            
                            appendStr += "<option " + selected + "value=\"" + code + "\">" + name + "</option>";
                        }
                        codeSelect.insertAdjacentHTML("beforeend", appendStr);
                    });
                });
			</script><%
        }
	%></ul>
    
    <script>
        function submitHash(form) {
            "use strict";
            form.formElement.submit(function () {
                if (form.submitted) {
                    return;
                }
                
                var hash = form.hashElement.val(),
                    redirectUrl = form.redirectUrl + ".html",
                    currentUrl = form.currentUrl + ".html",
                    queryPos = currentUrl.indexOf('?'),
                    queryStr,
                    hashPos;

                if (queryPos != -1) {
                    queryStr = currentUrl.substring(queryPos);
                    hashPos = queryStr.indexOf('#');
                    if (hashPos != -1) {
                        queryStr = queryStr.substr(0, hashPos);
                    }
                    redirectUrl += queryStr;
                }
                window.location.href = redirectUrl + '#' + hash;
                if (currentUrl == redirectUrl) {
                    window.location.reload();
                }

                form.submitted = true;
            });
        }
        
        $(document).ready(function () {
            "use strict";
            var hashForms = {
                zip: {
                    formElement: $(".council-finder form[name='zipSearch']"),
                    hashElement: $(".council-finder form[name='zipSearch'] input[name='zip']"),
                    redirectUrl: "<%=resourceResolver.map(path)%>",
                    currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>",
                    submitted: false
                },
                state: {
                    formElement: $(".council-finder form[name='stateSearch']"),
                    hashElement: $(".council-finder form[name='stateSearch'] select[name='state']"),
                    redirectUrl: "<%=resourceResolver.map(path)%>",
                    currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>",
                    submitted: false
                },
                councilCode: {
                    formElement: $(".council-finder form[name='councilCodeSearch']"),
                    hashElement: $(".council-finder form[name='councilCodeSearch'] select[name='council-code']"),
                    redirectUrl: "<%=resourceResolver.map(path)%>",
                    currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>",
                    submitted: false
                }
            }, form;
            
            for (form in hashForms) {
                if (hashForms.hasOwnProperty(form) && form.formElement) {
                    submitHash(form);
                }
            }
        });
    </script><%
} 
%>