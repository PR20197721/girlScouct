Girl Scouts Volunteer Toolkit
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


TROUBLESHOOTING
===============
On occassion, the replication between publish instance will fail on production.  This is a serious issue that can bring down the entire system.  Here is how to address the problem.  You will need to be online with Elham to access the replciation queue on both publish A and publish B.
1. On the trouble publish instance (say Publish A), check the replication queue.  The problem item is always the first replication event on the list.  Copy the path.
2. Go to Publish B and go to the path in the CRX Explorer view.
3. Right click on the item and lock the item.
4. Go back to Publish A and see if the queue is clearing. If not, then retry the replication item or delete it if all else fails.
5. Observe the replciation queue decrease and repeat for any other nodes.
