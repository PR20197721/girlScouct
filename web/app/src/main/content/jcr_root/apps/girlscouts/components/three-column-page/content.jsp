<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
	<!--PAGE STRUCTURE: LEFT CONTENT-->
	<div class="large-5 medium-5 hide-for-small columns mainLeft">
		<div id="leftContent">
			<cq:include script="left.jsp" />
		</div>
	</div>
	<div class="large-19 medium-19 small-24 columns mainRight">
		<div class="row">
			<div class="large-24 medium-24 hide-for-small columns rightBodyTop">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
		</div>
		<div>
			<div class="large-18 medium-18 small-24 columns rightBodyLeft">
				<!--PAGE STRUCTURE: MAIN CONTENT-->
				<cq:include script="middle.jsp" />
			</div>
			<!--PAGE STRUCTURE: RIGHT CONTENT-->
			<div id="rightContent" class="large-6 medium-6 small-24 columns">
				<cq:include script="right.jsp" />
			</div>
		</div>
	</div>
</div>
