<div class="BadgeExplorerPdfComponent">
	<div class="BadgePdfLogoWrapper canvasOutputElement">
		<h1>LOGO</h1>
	</div>
	<div class="BadgePdfHeaderTitle canvasOutputElement">My Badge Explorer</div>
	<div class="BadgePdfHeader canvasOutputElement">
		<div class="BadgePdfImageColumn">Badge</div>
		<div class="BadgePdfDescriptionColumn">DESCRIPTION</div>
	</div>
	<badge-explorer-element-pdf v-bind="badge" v-for="(badge, index) in badgeData"/>
</div>
<%--

	<div class="BadgePdfBody">
		<badge-explorer-element-pdf v-bind="badge" v-for="(badge, index) in badgeData" :class='{"html2pdf__page-break" : (((index + 1) % 2) == 0)}'/>
	</div>
	<div class="BadgePdfBody">
		<div v-for="(badge, index) in badgeData">
			<badge-explorer-element-pdf v-bind="badge" />
			<div v-if="index % 2 == 0" class="html2pdf__page-break"></div>
		</div>
	</div>
--%>