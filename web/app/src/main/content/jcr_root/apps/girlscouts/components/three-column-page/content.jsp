<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/include-options.jsp"%>

<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
	<!--PAGE STRUCTURE: LEFT CONTENT-->
	<div class="large-4 medium-5 hide-for-small columns mainLeft">
		<!--PAGE STRUCTURE: LEFT CONTENT-->
		<div id="leftContent">
			<cq:include path="content/left/cascading-menus" resourceType="girlscouts/components/cascading-menus" />
			<cq:include path="content/left/par" resourceType="foundation/components/parsys" />
		</div>
	</div>
	<div class="large-20 medium-19 small-24 columns mainRight">
		<div class="row">
			<div class="large-24 medium-24 hide-for-small columns rightBodyTop">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
		</div>
		<div>
			<div class="large-19 medium-19 small-24 columns rightBodyLeft">
				<!--PAGE STRUCTURE: MAIN CONTENT-->
				<div id="mainContent">
					<cq:include path="content/middle/par" resourceType="foundation/components/parsys" />
				</div>
			</div>
			<!--PAGE STRUCTURE: RIGHT CONTENT-->
			<div id="rightContent" class="large-5 medium-5 small-24 columns">
				<cq:include path="content/right/advertisement" resourceType="girlscouts/components/advertisement" />
			</div>
		</div>
	</div>
</div>
