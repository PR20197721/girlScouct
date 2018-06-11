<div class="BadgeExplorerPdfComponent">
	<%-- TODO: Replace with image tag after merge. --%>
	<div class="BadgePdfLogoWrapper canvasOutputElement"><img width="168px" src="/content/dam/girlscouts-gsusa/images/logo/logo_print@2x.png.img.png/_jcr_content/renditions/original.png" /></div>
	<div class="BadgePdfHeaderTitle canvasOutputElement">My Badge Explorer</div>
	<div class="BadgePdfHeader canvasOutputElement">
		<div class="BadgePdfImageColumn">BADGE</div>
		<div class="BadgePdfDescriptionColumn">DESCRIPTION</div>
		<div class="clearfix"></div>
	</div>
	<badge-explorer-element-pdf v-bind="badge" v-for="(badge, index) in badgeData"/>
</div>