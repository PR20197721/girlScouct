<div class="BadgeSelectorModal">
	<div class="CloseBadgeSelector" @click="close">X</div>
	<h1 class="BadgeSelectorTitle">Your Custom Badge List</h1>
	<br>
	<h5 class="BadgeSelectorSubtitle">Select individual badges or select all, then generate your PDF.</h5>
	<br>
	<div class="AddAllPdfContainer">
		<svg class="AddAllPdfOuter" viewBox="0 0 100 100" @click="selectAll">
			<circle stroke="#555" fill="#ddd" cx="50" cy="50" r="40" class="AddAllPdfCircleOuter"/>
			<circle stroke="#fff" fill="#ddd" cx="50" cy="50" r="35" class="AddAllPdfCircleInner"/>
			<circle stroke="#1c9346" fill="#1c9346" cx="50" cy="50" r="30" class="AddAllPdfCircleSelected" :class="{selected: allSelected}"/>
		</svg>
		<span><b>Add All to PDF</b></span>
	</div>
    <div class="CreateBadgePdfButton" @click="generatePdf" id="CreateBadgeButon">Generate PDF</div>
	<br>
    <div class="clearfix"></div>
    <div class="BadgeSelectionContainer">
		<badge-selector-element @selected="handleSelection" v-bind="badge" v-for="badge in badgeData"/>
	</div>
</div>