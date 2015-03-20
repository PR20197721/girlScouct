<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
		<!--PAGE STRUCTURE: LEFT CONTENT START-->
		<div class="large-5 hide-for-medium hide-for-small columns mainLeft">
			<div id="leftContent">
				<cq:include script="left.jsp" />
			</div>
		</div>
	        <!--PAGE STRUCTURE: LEFT CONTENT STOP-->

	        <!--PAGE STRUCTURE: MAIN CONTENT START-->
		<div class="large-19 medium-24 small-24 columns mainRight">
			<div class="breadcrumbWrapper">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
			<div class="row mainRightBottom">
				<div class="large-18 medium-18 small-24 columns rightBodyLeft">
					<!--PAGE STRUCTURE: MIDDLE CONTENT START-->
					<cq:include script="middle.jsp" />
	          <!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
				</div>
				<!--PAGE STRUCTURE: RIGHT CONTENT START-->
				<div id="rightContent" class="large-6 medium-6 small-24 columns">
					<cq:include script="right.jsp" />
				</div>
	      <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
			</div>
		</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
