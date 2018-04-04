<div class="BadgeExplorerPdfComponent">
	<div class="BadgePdfLogoWrapper">
		<h1>LOGO</h1>
	</div>
	<div class="BadgePdfHeaderTitle">My Badge Explorer</div>
	<div class="BadgePdfHeader">
		<div class="BadgePdfImageColumn">Badge</div>
		<div class="BadgePdfDescriptionColumn">DESCRIPTION</div>
	</div>
	<div class="BadgePdfBody">
		<badge-explorer-element-pdf v-for="badge in badgeData" v-bind="badge" />
	</div>
</div>