(function(){
	
	Vue.component('badge-explorer-pdf', {
		template: '#badge-explorer-pdf-template',
		props: ['badge-data']
	});
	
	Vue.component('badge-explorer-element-pdf', {
		template: '#badge-explorer-element-pdf-template',
		props: ['title', 'rank', 'image', 'description', 'link', 'other-tags'],
		data: function(){
			var cleanedDesc = (' ' + this.description).slice(1);
			var validHtml = false;
			try{
				validHtml = $(cleanedDesc).length > 0;
			}catch(e){/* invalid html */}
			if(!validHtml){
				var cleanedHtml = DomUtils.htmlDecode(cleanedDesc);
				if(cleanedHtml){
					cleanedDesc = cleanedHtml;
				}
			}
			
			return {
				descriptionCleaned: cleanedDesc
			};
		}
	});

	Vue.component('badge-selector', {
		template: '#badge-selector-template',
		data: function(){
			return {selectedBadges: [], selectors: []};
		},
		props: ['badge-data'],
		computed: {
			allSelected: function(){
				return this.selectedBadges.length == this.badgeData.length;
			}
		},
		methods: {
			handleSelection: function(ele, selected){
				if(selected){
					this.selectedBadges.push(ele);
				}else{
					this.selectedBadges.splice(this.selectedBadges.indexOf(ele), 1);
				}
			},
			generatePdf: function(){
				this.$emit('pdf', this.selectedBadges);
			},
			close: function(){
				this.$emit('close');
			},
			addSelector: function(selector){
				this.selectors.push(selector);
			},
			selectAll: function(){
				if(this.allSelected){
					for(var i = 0; i < this.selectors.length; i++){
						this.selectors[i].deselect();
					}
				}else{
					for(var i = 0; i < this.selectors.length; i++){
						this.selectors[i].select();
					}
				}
			}
		}
	});

	Vue.component('badge-selector-element', {
		template: '#badge-selector-element-template',
		props: ['title', 'rank', 'image', 'description', 'link', 'other-tags'],
		data: function(){
			return {selected: false}
		},
		methods: {
			toggleSelection: function(){
				this.selected = !this.selected;
				this.$emit('selected', this, this.selected);
			},
			select: function(){
				if(!this.selected){
					this.toggleSelection();
				}
			},
			deselect: function(){
				if(this.selected){
					this.toggleSelection();
				}
			}
		},
		mounted: function(){
			this.$parent.addSelector(this);
		}
	});
	
})();