<div class="BadgeExplorerPdfComponent">
	<div class="BadgePdfLogoWrapper canvasOutputElement">
		<h1>LOGO</h1>
	</div>
	<div class="BadgePdfHeaderTitle canvasOutputElement">My Badge Explorer</div>
	<div class="BadgePdfHeader canvasOutputElement">
		<div class="BadgePdfImageColumn">Badge</div>
		<div class="BadgePdfDescriptionColumn">DESCRIPTION</div>
	</div>
	<badge-explorer-element-pdf v-bind="badge" v-for="(badge, index) in badgeData" :key="badge.title"/>
</div>