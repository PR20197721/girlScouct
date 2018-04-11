<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<%-- <ui:includeClientLib categories="counsel.permissions.tool" /> --%>
<script src="/apps/girlscouts/components/counsel-permission-tool/clientlib.js"></script>
<link rel="stylesheet" type="text/css" href="/apps/girlscouts/components/counsel-permission-tool/clientlib.css"></link>

    
<div>
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
	        <counsel-folder class="FolderRow" v-for="folder in folders" :key="name + folder.path" @dam-folder-ok="damFolderOk = true"
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
			<select class="FolderPath" v-else-if="isDamFolder" v-model="selectedFolderPath">
	            <option class="FolderOptionNotAvailable" :value="path">{{folderPath}}</option>
				<option v-for="availableFolder in availableFolders" :value="availableFolder.path">{{availableFolder.path}}</option>
			</select>
			<div class="FolderPath" v-else>{{folderPath}} (not found)</div>
			<div class="FolderExists" :class="{PermissionsOK : exists, PermissionsBad : !exists,}">{{exists}}</div>
			<div class="PermissionsCheck" :class="{PermissionsOK : correctAuthorPermissions, PermissionsBad : !correctAuthorPermissions}">{{correctAuthorPermissions}}</div>
			<div class="PermissionsCheck" :class="{PermissionsOK : correctReviewerPermissions, PermissionsBad : !correctReviewerPermissions}">{{correctReviewerPermissions}}</div>
			<div class="FixitButton" v-show="needsFixing" @click="fix" :class="{FixButtonActive : ableToFix, FixButtonInactive : !ableToFix}">{{fixButtonText}}</div>
		</div>
	</script>
	
	<%-- Entry Point --%>
	<div id="CounselPermissionToolWrapper">
		<h2>Dam Directory Status</h2>
		<div class="CounselRow CounselRowCommon">
			<div v-if="counselsToFix.length > 0">
				<h3>The following counsels still need the DAM directory fixed...</h3>
				<p class="PermissionExplanation">
					<span>These counsels need explicit permissions to their /content/dam directories.</span>
					<br>
					<span>Once this is complete, return to this section to set global permissions for the Dam directory.</span>
					<br>
					<b>Note: Counsels with missing dam directories are not reported here.  Any *real* counsels should have their dam directories updated before proceeding.</b>
				</p>
				<ul>
					<li v-for="counsel in counselsToFix">{{counsel.name}}</li>
				</ul>
			</div>
			<div v-else>
				<h3>All Counsel DAM directories have been Fixed!</h3>
				<p class="PermissionExplanation Active">
					<span>Now that each Counsel has explicit access to their Dam directory the global permissions can be removed.</span>
					<br>
					<span>To do this, we need to remove the global <b>grant</b> permission, add a global <b>deny</b> permission, and remove all the counsel specific <b>deny</b> permissions</span>
				</p>
				<div class="FolderRow">
					<p class="PermissionExplanation" :class="{Active : needsDefaultDamPermissionRemoval}">This will remove the <b>ace:Grant</b> permission on /content/dam for the gs-authors and gs-reviewers groups.</p>
					<div class="FolderPath">Remove Allowed Root DAM Permissions for common groups?</div>
					<div class="PermissionsCheck" :class="{PermissionsOK : !needsDefaultDamPermissionRemoval, PermissionsBad : needsDefaultDamPermissionRemoval}">{{removeDamPermissionsText}}</div>
					<div class="FixitButton FixButtonActive" v-if="needsDefaultDamPermissionRemoval" @click="removeDefaultDamPermissions">Fix!</div>
				</div>
				<div class="FolderRow">
					<p class="PermissionExplanation" :class="{Active : (!needsDefaultDamPermissionRemoval && needsDefaultDamPermissionAddition)}">This will add the <b>ace:Deny</b> permission on /content/dam for the gs-authors and gs-reviewers groups.</p>
					<div class="FolderPath">Default Deny Root DAM Permissions for common user?</div>
					<div class="PermissionsCheck" :class="{PermissionsOK : !needsDefaultDamPermissionAddition, PermissionsBad : needsDefaultDamPermissionAddition}">{{addDamPermissionsText}}</div>
					<div class="FixitButton" v-if="needsDefaultDamPermissionAddition" :class="{FixButtonActive : !needsDefaultDamPermissionRemoval, FixButtonInactive : needsDefaultDamPermissionRemoval}" @click="addDefaultDamPermissions">Fix!</div>
					<div class="FixitButtonReason" v-if="needsDefaultDamPermissionRemoval">Remove Default Allow Permissions for Common Group First.</div>
				</div>
				<div class="FolderRow">
					<p class="PermissionExplanation" :class="{Active : needsDefaultVtkResourcesPermissionRemoval}">This will remove the <b>ace:Grant</b> permission on /content/resources2 for the gs-authors and gs-reviewers groups.</p>
					<div class="FolderPath">Remove Allowed Root VTK Resources2 Permissions for common groups?</div>
					<div class="PermissionsCheck" :class="{PermissionsOK : !needsDefaultVtkResourcesPermissionRemoval, PermissionsBad : needsDefaultVtkResourcesPermissionRemoval}">{{removeDamPermissionsText}}</div>
					<div class="FixitButton FixButtonActive" v-if="needsDefaultVtkResourcesPermissionRemoval" @click="removeDefaultVtkResourcesPermissions">Fix!</div>
				</div>
				<div class="FolderRow">
					<p class="PermissionExplanation" :class="{Active : (!needsDefaultVtkResourcesPermissionRemoval && needsDefaultVtkResourcesPermissionAddition)}">This will add the <b>ace:Deny</b> permission on /content/resources2 for the gs-authors and gs-reviewers groups.</p>
					<div class="FolderPath">Default Deny Root VTK Resources2 for common user?</div>
					<div class="PermissionsCheck" :class="{PermissionsOK : !needsDefaultVtkResourcesPermissionAddition, PermissionsBad : needsDefaultVtkResourcesPermissionAddition}">{{addDamPermissionsText}}</div>
					<div class="FixitButton" v-if="needsDefaultVtkResourcesPermissionAddition" :class="{FixButtonActive : !needsDefaultVtkResourcesPermissionRemoval, FixButtonInactive : needsDefaultVtkResourcesPermissionRemoval}" @click="addDefaultVtkResourcesPermissions">Fix!</div>
					<div class="FixitButtonReason" v-if="needsDefaultVtkResourcesPermissionRemoval">Remove Default Allow Permissions for Common Group First.</div>
				</div>
				<div class="FolderRow">
					<p class="PermissionExplanation" :class="{Active : (!needsDefaultDamPermissionRemoval && !needsDefaultDamPermissionAddition && deniedDamFolders.length > 0)}">This will remove all of the <b>ace:Deny</b> permission for the counsel folders under /content/dam/* for the gs-authors and gs-reviewers groups.</p>
					<div class="FolderPath">{{deniedDamFolders.length}} counsels-permissions are still explicitly denied.<span v-if="deniedDamFolders.length > 0">  Remove?</span></div>
					<div class="PermissionsCheck" :class="{PermissionsOK : deniedDamFolders.length < 1, PermissionsBad : deniedDamFolders.length > 0}">{{removeExplicitPermissionsText}}</div>
					<div class="FixitButton" v-if="deniedDamFolders.length > 0"  :class="{FixButtonActive : !needsDefaultDamPermissionAddition, FixButtonInactive : needsDefaultDamPermissionAddition}" @click="removeCounselDenials">Fix!</div>
					<div class="FixitButtonReason" v-if="needsDefaultDamPermissionAddition">Default Deny Permissions for Common Group First.</div>
				</div>
			</div>
		</div>
	
		<h2>Counsel Status</h2>
	    <counsel-state class="CounselRow" v-for="counsel in counsels" :key="counsel.name"
	    				:name="counsel.name" 
	    				:reviewer-name="counsel.reviewerName" 
	    				:author-name="counsel.authorName" 
	    				:folders="counsel.folders" 
	    				:available-folders="availableFolders">
	    	</counsel-state>
	</div>
</div>