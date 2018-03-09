<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="counsel.permissions.tool" />

<%-- Template represents counsel block. --%>
<script type="text/x-template" id="CounselTemplate">
	<div>
		<div class="CounselInfoWrapper">
			<div class="CounselInfoBlock">
				<div class="CounselInfoLabel">Counsel Name: </div>
				<div class="CounselName">{{name}}</div>
			</div>
			<div class="CounselInfoBlock" :class="{CounselInfoBroke : missingReviewer}">
				<div class="CounselInfoLabel">Reviwer Group: </div>
				<div class="ReviewerName">{{reviewerName}}</div>
			</div>
			<div class="CounselInfoBlock" :class="{CounselInfoBroke : missingAuthor}">
				<div class="CounselInfoLabel">Author Group: </div>
				<div class="AuthorName">{{authorName}}</div>
			</div>
		</div>
		<div class="FolderRow FolderRowHeader">
			<div class="FolderPath">Path</div>
			<div class="FolderExists"">Folder Exists?</div>
			<div class="PermissionsCheck">Author Permissions?</div>
			<div class="PermissionsCheck">Reviewer Permissions?</div>
			<div class="PermissionsCheck">Fix!</div>
		</div>
        <counsel-folder class="FolderRow" v-for="folder in folders" :key="name + folder.path"
				:path="folder.path" 
				:has-user-groups="!(missingReviewer || missingAuthor)"
				:correct-author-permissions="folder.correctAuthorPermissions" 
				:correct-reviewer-permissions="folder.correctReviewerPermissions" 
				:exists="folder.exists" 
				:available-folders="availableFolders"
				:reviewer-name="reviewerName"
				:author-name="authorName"
				:counsel-name="name"
		></counsel-folder>
	</div>
</script>

<%-- Template represents each folder-row. --%>
<script type="text/x-template" id="FolderTemplate">
	<div>
		<div class="FolderPath" v-if="exists">{{folderPath}}</div>
		<select class="FolderPath" v-else v-model="selectedFolderPath">
            <option class="FolderOptionNotAvailable" :value="path">{{folderPath}}</option>
			<option v-for="availableFolder in availableFolders" :value="availableFolder.path">{{availableFolder.path}}</option>
		</select>
		<div class="FolderExists" :class="{PermissionsOK : exists, PermissionsBad : !exists,}">{{exists}}</div>
		<div class="PermissionsCheck" :class="{PermissionsOK : correctAuthorPermissions, PermissionsBad : !correctAuthorPermissions}">{{correctAuthorPermissions}}</div>
		<div class="PermissionsCheck" :class="{PermissionsOK : correctReviewerPermissions, PermissionsBad : !correctReviewerPermissions}">{{correctReviewerPermissions}}</div>
		<div class="FixitButton" v-show="needsFixing" @click="fix" :class="{FixButtonActive : ableToFix, FixButtonInactive : !ableToFix}">{{fixButtonText}}</div>
	</div>
</script>

<%-- Entry Point --%>
<div id="CounselPermissionToolWrapper">
	<h2>Counsel Status</h2>
    <counsel-state class="CounselRow" v-for="counsel in counsels" :key="counsel.name" 
    				:name="counsel.name" 
    				:reviewer-name="counsel.reviewerName" 
    				:author-name="counsel.authorName" 
    				:folders="counsel.folders" 
    				:available-folders="availableFolders">
    	</counsel-state></counsel-state>
</div>