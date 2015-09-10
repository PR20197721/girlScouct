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
1. On the trouble publish instance (say Publish A), check the replication queue.  The problem item is usually first replication event on the list and ususally a folder called yearPlan.  Copy the path.
2. Go to Publish B and go to the path in the CRX Explorer view.
3. Right click on the item and lock the item.
4. Go back to Publish A and see if the queue is clearing. If not, then force retry the replication item or delete it if all else fails.
5. Observe the replciation queue decrease and repeat for any other nodes.

Another approach.  If Publish A has, say 80,000 items in replication queue and no matter what you do it doesn't go back down.  
1. Make sure that Publish B has a clear replication queue.  If not, then you can't use this method or you will lose data.
2. Take Publish B out of the load balancer.
3. Turn off the replication agent on Publish A and B
4. Go to package manager in Publish A and create a package of current year vtk folder (e.g. /vtk-2015)
5. Delete all replication items in Publish A replication queue turn on replication agent 
6. Go to Publish B and delete the current year vtk folder and replace by installing the package you just created on Publish A.
7. Turn on the replication agent on Publish B

