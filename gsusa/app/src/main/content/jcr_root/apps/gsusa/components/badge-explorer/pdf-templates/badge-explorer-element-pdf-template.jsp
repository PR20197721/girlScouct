<div class="BadgeExplorerElementPdfComponent canvasOutputElement">
	<div class="BadgePdfImageColumn">
		<img :src="image" />
		<div class="BadgePdfTitle">{{title}}</div>
		<div class="BadgeRankLevel">{{rank}}</div>
		<div class="BadgeSubjectMatter" v-for="subject in otherTags">{{subject}}</div>
	</div>
	<div class="BadgePdfDescriptionColumn">
		<div class="BadgePdfDescription" v-html="descriptionCleaned"></div>
		<div class="BadgePdfGetContainer" v-if="link">
			<a :href="link">{{badgeCTA}}</a>
		</div>
	</div>
</div>