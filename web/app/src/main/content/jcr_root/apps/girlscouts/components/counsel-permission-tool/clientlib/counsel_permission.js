$(function(){
	
	var ServerDataManager = (function(){
		// Internal reference to the data on the widget.
		var serverData;

		// First off get the data!
		$.ajax('/bin/utils/counsel_permission_status').then(function(initialServerData){
			serverData = initialServerData;

			// Ensure server data has all the components at least.
            if(!serverData.counsels){
				serverData.counsels = [];
                console.warn("No Counsel Data returned!!");
            }
            if(!serverData.availableFolders){
				serverData.availableFolders = [];
                console.warn("No available folders returned!!");
            }
            if(!serverData.deniedDamFolders){
            		serverData.deniedDamFolders = [];
            		console.warn("No denied dam folders returned!");
            }
            
            // Setup the counsel list.
            serverData.counselList = [];

			new Vue({
				el: '#CounselPermissionToolWrapper',
				data: serverData,
				computed: {
					counselsToFix: function(){
						var returner = [];
						for(var i = 0; i < this.counselList.length; i++){
							if(!this.counselList[i].damFolderOk){
								returner.push(this.counselList[i]);
							}
						}
						return returner;
					},
					needsDefaultDamPermissionRemoval: function(){
						return this.damPermissionSetting == 'Allowed' || this.damPermissionSetting == 'Both';
					},
					needsDefaultVtkResourcesPermissionRemoval: function(){
						return this.vtkResourcesPermissionSetting == 'Allowed' || this.vtkResourcesPermissionSetting == 'Both';
					},
					needsDefaultDamPermissionAddition: function(){
						return this.damPermissionSetting != 'Denied' && this.damPermissionSetting != 'Both';
					},
					needsDefaultVtkResourcesPermissionAddition: function(){
						return this.vtkResourcesPermissionSetting != 'Denied' && this.vtkResourcesPermissionSetting != 'Both';
					},
					removeDamPermissionsText: function(){
						return this.needsDefaultDamPermissionRemoval ? "Not Removed" : "Removed!";
					},
					addDamPermissionsText: function(){
						return this.needsDefaultDamPermissionAddition ? "Not Added" : "Added!";
					},
					removeExplicitPermissionsText: function(){
						return this.deniedDamFolders.length > 0 ? (this.deniedDamFolders.length + " Denys") : "Removed!";
					}
				},
				methods: {
					registerCounsel: function(newCounsel){
						this.counselList.push(newCounsel);
					},
					removeDefaultDamPermissions: function(){
						if(!this.needsDefaultDamPermissionRemoval){
							return;
						}
						
						$.ajax('/bin/utils/counsel_permission_remove_default_dam_permissions', {
							data: {
								inputData: JSON.stringify({
									requestedFolders: ['/content/dam'],
									pathOverride: false,
									reviewerGroupName: 'gs-reviewers',
									authorGroupName: 'gs-authors',
									counselName: 'common-counsel'
								})
							}
						}).then(function(responseJson){
							ServerDataManager.setServerData(responseJson);
						}, function(){
							alert('Failed!');
						});
					},
					removeDefaultVtkResourcesPermissions: function(){
						if(!this.needsDefaultVtkResourcesPermissionRemoval){
							return;
						}
						
						$.ajax('/bin/utils/counsel_permission_remove_default_dam_permissions', {
							data: {
								inputData: JSON.stringify({
									requestedFolders: ['/content/vtk-resources2'],
									pathOverride: false,
									reviewerGroupName: 'gs-reviewers',
									authorGroupName: 'gs-authors',
									counselName: 'common-counsel'
								})
							}
						}).then(function(responseJson){
							ServerDataManager.setServerData(responseJson);
						}, function(){
							alert('Failed!');
						});
					},
					addDefaultDamPermissions: function(){
						if(!this.needsDefaultDamPermissionAddition || this.needsDefaultDamPermissionRemoval){
							return;
						}
						
						$.ajax('/bin/utils/counsel_permission_update_deny_default_dam_permissions', {
							data: {
								inputData: JSON.stringify({
									requestedFolders: ['/content/dam'],
									pathOverride: false,
									reviewerGroupName: 'gs-reviewers',
									authorGroupName: 'gs-authors',
									counselName: 'common-counsel'
								})
							}
						}).then(function(responseJson){
							ServerDataManager.setServerData(responseJson);
						}, function(){
							alert('Failed!');
						});
					},
					addDefaultVtkResourcesPermissions: function(){
						if(!this.needsDefaultVtkResourcesPermissionAddition || this.needsDefaultVtkResourcesPermissionRemoval){
							return;
						}
						
						$.ajax('/bin/utils/counsel_permission_update_deny_default_dam_permissions', {
							data: {
								inputData: JSON.stringify({
									requestedFolders: ['/content/vtk-resources2'],
									pathOverride: false,
									reviewerGroupName: 'gs-reviewers',
									authorGroupName: 'gs-authors',
									counselName: 'common-counsel',
									allowRoot: true
								})
							}
						}).then(function(responseJson){
							ServerDataManager.setServerData(responseJson);
						}, function(){
							alert('Failed!');
						});
					},
					removeCounselDenials: function(){
						if(this.deniedDamFolders.length < 1 || this.needsDefaultDamPermissionAddition){
							return;
						}					
						
						var damFolderPaths = [];
						for(var i = 0; i < this.deniedDamFolders.length; i++){
							damFolderPaths.push(this.deniedDamFolders[i].path);
						}	
						
						$.ajax('/bin/utils/counsel_permission_update_remove_counsel_dam_denial_nodes', {
							data: {
								inputData: JSON.stringify({
									requestedFolders: damFolderPaths,
									pathOverride: false,
									reviewerGroupName: 'gs-reviewers',
									authorGroupName: 'gs-authors',
									counselName: 'common-counsel'
								})
							}
						}).then(function(responseJson){
							ServerDataManager.setServerData(responseJson);
						}, function(){
							alert('Failed!');
						});
					}
				}
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
				if(newData && newData.damPermissionSetting){
					serverData.damPermissionSetting = newData.damPermissionSetting;
				}
				if(newData && newData.deniedDamFolders){
					serverData.deniedDamFolders = newData.deniedDamFolders;
				}
				if(newData && newData.vtkResourcesPermissionSetting){
					serverData.vtkResourcesPermissionSetting = newData.vtkResourcesPermissionSetting;
				}
			}
		}
	
	})();
	
	Vue.component('counsel-state', {
		template: '#CounselTemplate',
		props: ['name', 'reviewerName', 'authorName', 'folders', 'availableFolders'],
		data: function(){
			// Represents if the Dam folder is fixed (or if it's not fixable)
			return {damFolderOk: this.name == 'common-counsel'}
		},
		computed: {
			missingReviewer: function(){
				return this.reviewerName == 'UNKNOWN';
			},
			missingAuthor: function(){
				return this.authorName == 'UNKNOWN';
			}
		},
		created: function(){
			this.$parent.registerCounsel(this);
		}
	});
	
	Vue.component('counsel-folder', {
		template: '#FolderTemplate',
		props: ['path', 'hasUserGroups', 'counselName', 'exists', 'correctAuthorPermissions', 'correctReviewerPermissions', 'availableFolders', 'reviewerName', 'authorName'],
		data: function(){
			return {
				selectedFolderPath : this.path, 
				fixing: false,
				isDamFolder: this.path.indexOf('/content/dam') > -1
			}
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
				$.ajax('/bin/utils/counsel_permission_update_add_missing_rights', {
					data: {
						inputData: JSON.stringify({
							requestedFolders: [this.selectedFolderPath],
							pathOverride: !!(this.selectedFolderPath != this.path),
							reviewerGroupName: this.reviewerName,
							authorGroupName: this.authorName,
							counselName: this.counselName
						})
					}
				}).then(function(responseJson){
					ServerDataManager.setServerData(responseJson);
					thisVm.fixing = false;
					thisVm.checkDamFolderOk();
				}, function(){
					thisVm.fixing = false;
					alert('Failed!');
				});
				
			},
			checkDamFolderOk: function(){
				Vue.nextTick($.proxy(function(){
					if(this.isDamFolder && (!this.ableToFix || !this.needsFixing)){
						this.$emit('dam-folder-ok');
					}
				}, this));
			}
		},
		created: function(){
			this.checkDamFolderOk();
		}
	});
	
});