AEM 6.1 Data Migration Plan
==========================

Overview
--------
This is the plan to migrate council data from AEM 5.6.1 prod to AEM 6.1 prod in batch. We will adopt a modified version of jackrabbit filevault tool to migrate DAM assets, since it supports delta migration. We will also leverage CRX package manager to migrate other data including content nodes, designs and tags.
The entire process for each batch (roughly 10 councils at a time) will take two days. The first day, which will be the day before the DNS cutover, we will do the heavy lifting: migrating all content from AEM 5.6.1 to AEM 6.1 using the tools mentioned above. Because we will be doing the content migration, a content freeze is imposed. The second day, we will be on the call with the councils to go through the DNS cutover, one at a time. Content freeze is lifted once the DNS cutover is completed.

The Day Before DNS Cutover
--------------------------
### Step 1. Kick off content freeze
Notify and confirm with the affected coucils that the content freeze has started.

### Step 2. Backup the both AEM 5.6.1 and AEM 6.1 author and publishers using CRX cloud backup

### Step 3. Turn off workflow launchers on AEM 6.1 instance
com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl
com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener

*Repeat step 3 to step 14 for each council*:

### Step 4. Migrate data from AEM 5.6.1 author to AEM 6.1 author using vlt
*prerequisite: port 4502 of AEM 5.6.1 author should be reachable to AEM 6.1 author.

SSH into *AEM 6.1 author* and execute the following commands:
export AEM56=<aem_5.6_ip> (See appendix A for IP addresses)
export PORT=<4502/4503>
export COUNCIL_DAM=<council_dam> (<council_dam> should be the folder name under /content/dam, e.g. southern-appalachian.)
export PASSWORD=<password>
./vlt rcp -r -u -n 'http://admin:$PASSWORD@AEM56:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM' 'http://admin:$PASSWORD@localhost:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM' 2>&1 | tee vlt.log

### Step 5. Create a content package on AEM 5.6.1 author
The name of this package should be: <council_nam>-content
The version should be: YYYYMMDD_1
Use the following filters:
/content/<council>
/content/dam/<council> -- some councils use inconsistent directory names
/etc/designs/girlscouts-<council>
/etc/scaffolding/<council>
/etc/tags/<council>

### Step 6. Upload and install the content on AEM 6.1 author

### Step 7. Migrate data from AEM 5.6.1 publish1 to AEM 6.1 publish 1 and publish 2
*prerequisite: port 4503 of AEM 5.6.1 publish1 should be reachable to AEM 6.1 publish 1 and publish 2.

SSH into *AEM 6.1 publish* and execute the commands mentioned in step 4.

### Step 8. Create a content package on AEM 5.6.1 publish 1
Use the same filters in step 5.

### Step 9. Upload and install the content on AEM 6.1 publish 1 and publish 2

### Step 10. Clear the dispatcher cache on publish 1 and publish 2
/content/<council>
/etc/designs/girlscouts-<council>

### Step 11. Migrate data from AEM 5.6.1 preview to AEM 6.1 preview
*prerequisite: port 4503 of AEM 5.6.1 preview should be reachable to AEM 6.1 preview.

SSH into *AEM 6.1 preview* and execute the commands mentioned in step 4.

### Step 12. Create a content package on AEM 5.6.1 preview 
Use the same filters in step 5.

### Step 13. Upload and install the content on AEM 6.1 preview 

### Step 14. Clear the dispatcher cache on preview
/content/<council>
/etc/designs/girlscouts-<council>

### Step 15. Smoke test both publishers and preview

*One council completed.*
