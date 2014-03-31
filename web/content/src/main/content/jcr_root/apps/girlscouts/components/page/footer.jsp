<%@include file="/libs/foundation/global.jsp" %>
<%
	String contentPath = currentPage.getAbsoluteParent(2).getContentResource().getPath();
	String parPath = contentPath + "/footer/par";
%>
<!--PAGE STRUCTURE: FOOTER-->
                <div id="footer" class="row">
                    <div class="large-24 medium-24 small-24 columns">
                        <a class="menu" href="/privacy">Privacy Policy</a>
                        <a class="menu" href="/terms">Terms and Conditions</a>
<cq:include path="<%= parPath %>" resourceType="foundation/components/parsys" />
                    </div>
                </div>
            </div>
        </div>
    <script src="js/vendor/jquery.js"></script>
    <script src="js/foundation.min.js"></script>
    <script>
      $(document).foundation();
    </script>
