<div class="badge-block" @click="toggleSelection" :class="{BadgeSelected : selected}">
	<div class="badge-content">
		<div class="badge-body">
			<label class="badge-image-wrapper">
				<div class="badge-image-body">
					<div class="badge-image-inner">
						<img class="badge-image" :alt="title" :src="image">
					</div>
				</div>
			</label>
			<div class="BadgeSelectedOverlay" v-if="selected">
				<i class="icon-check"></i>
				<div class="BadgeSelectedOverlayText">Added to PDF</div>
			</div>
		</div>
		<div class="badge-title-wrapper">
			<div class="badge-title-body">
				<label class="badge-title">{{title}}</label>
			</div>
		</div>
	</div>
</div>