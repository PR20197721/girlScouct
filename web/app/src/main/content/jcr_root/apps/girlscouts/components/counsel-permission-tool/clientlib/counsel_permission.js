$(function(){
	
	var ServerDataManager = (function(){
		// Internal reference to the data on the widget.
		var serverData;

		// First off get the data!
		$.ajax('/bin/utils/counsel_permission_status').then(function(initialServerData){
			serverData = initialServerData;
			new Vue({
				el: '#CounselPermissionToolWrapper',
				data: serverData
			});
		});
		
		// Return access to change the page data but limit to what we know about.
		return {
			setServerData: function(newData){
				if(newData && newData.counsels){
					serverData.counsels = newData.counsels;
				}
				if(newData && newData.availableFolders){
					serverData.availableFolders = newData.availableFolders;
				}
			}
		}
	
	})();
	
	Vue.component('counsel-state', {
		template: '#CounselTemplate',
		props: ['name', 'reviewerName', 'authorName', 'folders', 'availableFolders'],
		computed: {
			missingReviewer: function(){
				return this.reviewerName == 'UNKNOWN';
			},
			missingAuthor: function(){
				return this.authorName == 'UNKNOWN';
			}
		}
	});
	
	Vue.component('counsel-folder', {
		template: '#FolderTemplate',
		props: ['path', 'hasUserGroups', 'counselName', 'exists', 'correctAuthorPermissions', 'correctReviewerPermissions', 'availableFolders', 'reviewerName', 'authorName'],
		data: function(){
			return {selectedFolderPath : this.path, fixing: false}
		},
		computed: {
			folderPath: function(){
				return this.path + (this.exists ? '' : '  (unable to locate)');
			},
			needsFixing: function(){
				return !this.correctAuthorPermissions || !this.correctReviewerPermissions;
			},
			ableToFix: function(){
				return this.hasUserGroups && ((this.selectedFolderPath != this.path) || this.exists);
			},
			fixButtonText: function(){
				if(this.fixing){
					return '.....';
				}else{
					return 'Fix!';
				}
			}
		},
		methods: {
			fix: function(){
				if(!this.ableToFix){
					return;
				}
				
				this.fixing = true;
				var thisVm = this;
				$.ajax('/bin/utils/counsel_permission_update', {
					data: {
						path: this.selectedFolderPath,
						pathOverride: !!(this.selectedFolderPath != this.path),
						reviewerGroup: this.reviewerName,
						authorGroup: this.authorName,
						counsel: this.counselName
					}
				}).then(function(responseJson){
					ServerDataManager.setServerData(responseJson);
					thisVm.fixing = false;
				}, function(){
					thisVm.fixing = false;
					alert('Failed!');
				});
				
			}
		}
	});
	
});