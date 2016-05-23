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



YEAR PLAN SETUP
===============

Every year in Spring, we are required to create new year plan templates for girl scouts. Here are the steps.

** Data Changes **
1. Go to Production and Down Sync all the current year plan templates and meetings to UAT. You can simply create a package and install on UAT. The path are listed here: 
  1. /content/girlscouts-vtk/meetings/myyearplan2015
  2. /content/girlscouts-vtk/yearPlanTemplates/yearplan2015
  3. /content/dam/girlscouts-vtk
2. For each of the above link, change the followings from 15 to 16 in UAT author.
  1. The name of the nodes. For example, myyearpaln2015 => myyearplan2016
  2. For all the child nodes under /content/girlscouts-vtk/meetings/myyearplan2016. Change their name prefix from B15 to B16. For exmaple: B15B01 => B16B01 or J15S01 => J16S01
  3. For all the child nodes under /content/girlscouts-vtk/yearPlanTemplates/yearplan2016. For each level, look for the meetings of the year plan. For each of these meetings, look for th property refId and change all the 2015 to 2016. For example, under /content/girlscouts-vtk/yearPlanTemplates/yearplan2014/junior/yearPlan1/meetings, there are 15 meetings. Each of them have a property refId like /content/girlscouts-vtk/meetings/myyearplan/junior/J15B01. change it to /content/girlscouts-vtk/meetings/myyearplan/junior/J16B01
3. In the vtk dam, /content/dam/girlscouts-vtk/local/icon/meetings, there are all these icons. Copy a the set of the icons from year 15, and paste it to the same place with the new year prefix 16. For example, Copy B15B05.png and paste it under the same location, rename it to B16B05.png
4. Check all the nodes in scaffolding /etc/scaffolding/girlscouts-vtk/landing.html. Make sure all the nodes are working properly 
5. Replicate all the new data to UAT publish.
6. Now we will need to change the configuration for the holidays and the girlscouts new year 
7. Once everything is ready, we can create a package and move it to Production environment.

** Code Change **
Under the path vtk/app/src/main/content/jcr_root/apps/girlscouts-vtk/wcm/components/vtk-scaffolding-landing/vtk-scaffolding-landing.jsp, there are two variables for the year plan, MEETING_BASE and YEAR_PLAN_BASE. Change it from 2015 to 2016. Check in your code.
