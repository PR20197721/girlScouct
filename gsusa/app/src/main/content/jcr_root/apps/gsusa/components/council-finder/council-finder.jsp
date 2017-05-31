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
                        <div>
                            <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="zip-code" placeholder="Enter ZIP Code" />
                        </div>
                        <div>
                            <input type="submit" value="Go" class="button tiny" />
                        </div>
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
					<p>Find a Girl Scout<br/> Council by Council Name</p>
                    <section>
                        <div>
                            <select required name="council-code">
                                <option value="">Select a Council:</option>
                            </select>
                        </div>
                        <div>
                            <input type="submit" value="Go" class="button tiny"/>
                        </div>
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
                            selected = request == code ? "selected='selected' " : "";
                            
                            appendStr += "<option " + selected + "value='" + code + "'>" + name + "</option>";
                        }
                        codeSelect.insertAdjacentHTML("beforeend", appendStr);
                    });
                });
			</script><%
        }
	%></ul>
    
    <script>
        $(document).ready(function () {
            bindSubmitHash({    // zip
                formElement: ".council-finder form[name='zipSearch']",
                hashElement: "input[name='zip-code']",
                redirectUrl: "<%=resourceResolver.map(path)%>",
                currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>"
            });
            bindSubmitHash({    // state
                formElement: ".council-finder form[name='stateSearch']",
                hashElement: "select[name='state']",
                redirectUrl: "<%=resourceResolver.map(path)%>",
                currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>"
            });
            bindSubmitHash({    // council-code
                formElement: ".council-finder form[name='councilCodeSearch']",
                hashElement: "select[name='council-code']",
                redirectUrl: "<%=resourceResolver.map(path)%>",
                currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>"
            });
        });
    </script><%
} 
%>