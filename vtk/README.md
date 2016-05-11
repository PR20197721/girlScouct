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

- Check the size of the replication queue and identify the server that has a long replication queue in the vtk-agent. Say this is server A. 
- Go to the other server (server B) and restart it.


SALESFORCE
==========
VTK requires salesforce for authentication and SSO.  

SF PROD: 
login.salesforce.com
gsvtk@girlscouts.org
icruise12345


SF UAT:
test.salesforce.com
alex_yakobovich@northps.com
icruise123



BOOTSTRAP DATA
==============
The bootstrap sub project should be executed on a virgin instance of AEM to create basic data needed to run VTK.  DO NOT upload this package onto any production environment as it will overwrite production content.
