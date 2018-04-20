<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div id="main" class="content row">
		<div class="large-24 medium-24 small-24 columns">
            <h1><%=currentPage.getTitle()%></h1>
			<div class="image parbase">
			<cq:include path="image" resourceType="girlscouts/components/image" />
            <cq:include path="text" resourceType="girlscouts/components/text"/>
            </div>


			</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
