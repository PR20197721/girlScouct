AEM 6.1 Data Migration Plan
==========================

Overview
--------
This is the plan to migrate council data from AEM 5.6.1 prod to AEM 6.1 prod in batch. We will adopt a modified version of jackrabbit filevault tool to migrate DAM assets, since it supports delta migration. We will also leverage CRX package manager to migrate other data including content nodes, designs and tags.
The entire process for each batch (roughly 10 councils at a time) will take two days. The first day, which will be the day before the DNS cutover, we will do the heavy lifting: migrating all content from AEM 5.6.1 to AEM 6.1 using the tools mentioned above. Because we will be doing the content migration, a content freeze is imposed. The second day, we will be on the call with the councils to go through the DNS cutover, one at a time. Content freeze is lifted once the DNS cutover is completed.

The Day Before DNS Cutover
--------------------------
### Step 1. Kick off content freeze
Notify and confirm with the affected councils that the content freeze has started.

### Step 2. Backup the both AEM 5.6.1 and AEM 6.1 author and publishers using CRX cloud backup

### Step 3. Turn off workflow launchers on AEM 6.1 instance
com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl
com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener

### Step 3.5 create event replication queue in AEM5.6 to replicate event AEM6.1(destination)
 copy /etc/replication/agents.author/publish1useast1 to create /etc/replication/agents.author/event61publish1

 copy /etc/replication/agents.author/publish1useast1 to create /etc/replication/agents.author/event61publish2

**In each replication queue, make the following two changes:
(1) update jcr:title to event61publish1 in /etc/replication/agents.author/event61publish1/jcr:content 
update jcr:title to event61publish2 in /etc/replication/agents.author/event61publish2/jcr:content

(2) update transportUri to http://[61 pub publish 1 and 2 ipaddress]/bin/vtk-receive?......

### Step 3.7 create package for the following filters for ALL councils existing in CRX
/content/{council_id}/en/events-repository
/content/girlscouts-template

**Repeat step 4 to step 14 for each council:**

### Step 4. Migrate data from AEM 5.6.1 author to AEM 6.1 author using vlt
*prerequisite: port 4502 of AEM 5.6.1 author should be reachable to AEM 6.1 author.*

SSH into *AEM 6.1 author* and execute the following commands:
export AEM56=[aem_5.6_ip] (See appendix A for IP addresses)
export PORT=[4502/4503]
export COUNCIL_DAM=[council_dam] ([council_dam] should be the folder name under /content/dam, e.g. southern-appalachian.)
export PASSWORD=[password]
./vlt rcp -r -u -n 'http://admin:$PASSWORD@AEM56:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM' 'http://admin:$PASSWORD@localhost:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM' 2>&1 | tee vlt.log

**for shared dam assets
./vlt rcp -r -u -n 'http://admin:$PASSWORD@AEM56:$PORT/crx/-/jcr:root/content/dam/girlscouts-shared' 'http://admin:$PASSWORD@localhost:$PORT/crx/-/jcr:root/content/dam/girlscouts-shared' 2>&1 | tee vlt.log

### Step 5. Create a content package on AEM 5.6.1 author
*1 content package will be created for all 10 sites*
The name of this package should be: [council_name]-content
The version should be: YYYYMMDD_1
Use the following filters:
/content/[[council]]
/content/usergenerated/{council_id}
/etc/designs/girlscouts-[council]
/etc/scaffolding/[council]
/etc/tags/[council]

### Step 6. Upload and install the content on AEM 6.1 author

### Step 7. Migrate data from AEM 5.6.1 publish1 to AEM 6.1 publish 1 and publish 2
*prerequisite: port 4503 of AEM 5.6.1 publish1 should be reachable to AEM 6.1 publish 1 and publish 2.*

SSH into *AEM 6.1 publish* and execute the commands mentioned in step 4.

### Step 8. Create a content package on AEM 5.6.1 publish 1
Use the same filters in step 5.

### Step 9. Upload and install the content on AEM 6.1 publish 1 and publish 2

### Step 10. Clear the dispatcher cache on publish 1 and publish 2
/content/[council]
/etc/designs/girlscouts-[council]

### Step 11. Migrate data from AEM 5.6.1 preview to AEM 6.1 preview
*prerequisite: port 4503 of AEM 5.6.1 preview should be reachable to AEM 6.1 preview.*

SSH into *AEM 6.1 preview* and execute the commands mentioned in step 4.

### Step 12. Create a content package on AEM 5.6.1 preview 
Use the same filters in step 5.

### Step 13. Upload and install the content on AEM 6.1 preview 

### Step 14. Clear the dispatcher cache on preview
/content/[council]
/etc/designs/girlscouts-[council]

### Step 15. Smoke test both publishers and preview

### Step 16. Turn workflow launchers backon on AEM 6.1 instance

**One council completed.**

DNS Cutover
-----------
### Step 1. Be on the phone with the council one by one and guide them through the DNS cutover procedure

### Step 2. Wait for the DNS cutover happens

### Step 3. Smoke test

### Step 4. CHEERS! :smile:

Appendix A: IP Addresses
------------------------
              AEM 5.6.1 / src    AEM 6.1 / dst
author        54.84.115.158      52.71.87.139
publish1      54.85.131.182      <unknown so far>
publish2      54.86.13.118       <unknown so far>
dispatcher1   54.84.146.54       <unknown so far>
dispatcher2   54.84.248.23       <unknown so far>

Appendix B: Jackrabbit Filevault customization
----------------------------------------------
commit a90e5e3afd8174bd4be2ce8a9cb949c4be6586a7
Author: Mike Zou <mzou@northpointdigital.com>
Date:   Wed Mar 16 13:28:07 2016 -0400

    Catch exception when ordering is not supported

diff --git a/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java b/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
index edb9c48..24bc62e 100644
--- a/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
+++ b/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
@@ -475,7 +475,11 @@ public class RepositoryCopier {
                             Node child = niter.nextNode();
                             String name = child.getName();
                             if (dst.hasNode(name)) {
-                                dst.orderBefore(name, null);
+                                try {
+                                    dst.orderBefore(name, null);
+                                } catch (RepositoryException e) {
+                                    log.warn("Ordering is not supported.");
+                                }
                             }
                         }
                     }


commit 59e71f288b027c8d6f8a6ecd8fa79898f0fa784f
Author: Mike Zou <mzou@northpointdigital.com>
Date:   Wed Mar 16 13:27:06 2016 -0400

    Check jcr:content for last modified date for dam:Asset and cq:Page nodes

    So, if you use -n flag, these nodes won't be replicated if nothing have changed.

diff --git a/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java b/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
index dd7e696..edb9c48 100644
--- a/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
+++ b/tools/java/jackrabbit-filevault-custom/vault-core/src/main/java/org/apache/jackrabbit/vault/util/RepositoryCopier.java
@@ -320,7 +320,23 @@ public class RepositoryCopier {
             if (skip) {
                 track(path, "------ S");
             } else if (overwrite) {
-                if (onlyNewer && dstName.equals("jcr:content")) {
+                String nodeType = null;
+                try {
+                    nodeType = src.getPrimaryNodeType().getName();
+                } catch (RepositoryException e) {
+                    log.warn("Cannot get primary node type for node: " + src.getPath());
+                }
+
+                if (onlyNewer && ("dam:Asset".equals(nodeType) || "cq:Page".equals(nodeType))) {
+                    if (src != null && src.hasNode("jcr:content") && dst != null && dst.hasNode("jcr:content") &&
+                            isNewer(src.getNode("jcr:content"), dst.getNode("jcr:content"))) {
+                        track(dstPath, "%06d U", ++totalNodes);
+                    } else {
+                        overwrite = false;
+                        recursive = false;
+                        track(dstPath, "%06d *", ++totalNodes);
+                    }
+                } else if (onlyNewer && dstName.equals("jcr:content")) {
                     if (isNewer(src, dst)) {
                         track(dstPath, "%06d U", ++totalNodes);
                     } else {
@@ -606,4 +622,4 @@ public class RepositoryCopier {
     public CredentialsProvider getCredentialsProvider() {
         return credentialsProvider;
     }
-}
\ No newline at end of file
+}
