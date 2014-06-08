Girl Scouts USA
========

This a content package project generated using the multimodule-content-package-archetype.

Building
--------

This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.

Using with VLT
--------------

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.

Specifying CRX Host/Port
------------------------

The CRX host and port can be specified on the command line with:
mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>


Dam Health Check
----------------

To resolve inconsistencies between the dam records in /var and /content, run the health checker (http://localhost:4502/etc/dam/healthchecker.html) and decide whether delete or synchronize the anomalies.  This will be necessary particularly when importing DAM assets through the package manager.

If you find that after importing a package in the DAM that renditions are not created, you will need to manually navigate to the item in the DAM (e.g.http://localhost:4502/damadmin#/content/dam/girlscouts-shared/en/banners/big/welcome2.png), click Edit... and save (without any changes).  This will re-run the corresponding rendition workflow.  If the renditions are not generated, you likely have STALE workflows that need to be cleared (usually resulting from flawed content imports).  Go to http://localhost:4502/libs/cq/workflow/content/console.html Instances tab and terminate or restart workflows so that the queue is able to reach the asset rendition workflows.


Fixing the internal Lucene Index
--------------------------------
CQ relies on a Lucene indexes to properly maintain functionality.  Importing content may cause these indexes to get out of sync with new and deleted files.  To completely rebuild the index, simply remove the following folder in your running CQ instance:
	crx-quickstart/repository/workspaces/crx.default/index/

If you want to repair the indexes, modify crx-quickstart/repository/workspaces/crx.default/workspace.xml by appending the three parammeters below accordingly and restart:

<SearchIndex class="com.day.crx.query.lucene.LuceneHandler">
    ... existing config
    <param name="enableConsistencyCheck" value="true" />
    <param name="forceConsistencyCheck" value="true" />
    <param name="autoRepair" value="true" />
</SearchIndex>

Remove the three parameters when complete or the repair and check will re-run every time the app starts up.
