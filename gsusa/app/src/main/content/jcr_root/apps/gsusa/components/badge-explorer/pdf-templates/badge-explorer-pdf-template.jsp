<div class="BadgeExplorerPdfComponent">
	<%-- TODO: Replace with image tag after merge. --%>
	<div class="BadgePdfLogoWrapper canvasOutputElement"><img src="/content/dam/girlscouts-gsusa/images/logo/logo_print.png.img.png" /></div>
	<div class="BadgePdfHeaderTitle canvasOutputElement">My Badge Explorer</div>
	<div class="BadgePdfHeader canvasOutputElement">
		<div class="BadgePdfImageColumn">Badge</div>
		<div class="BadgePdfDescriptionColumn">DESCRIPTION</div>
	</div>
	<badge-explorer-element-pdf v-bind="badge" @toggleSelection v-for="(badge, index) in badgeData"/>
</div>