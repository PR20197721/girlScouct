Girlscouts
==========

Girl Scouts web platform and Volunteer Toolkit code base.


Basic intro to CQ/AEM
======================

## Prerequisites
- java 1.5+
- maven 3.0.4+
- AEM (aka CQ) 6.0.0+
- Eclipse + the VaultClipse plugin



## Your first application
1. Create a folder for AEM projects and set the environment variable AEM_HOME (e.g. AEM_HOME=/usr/local/aem).  This folder will contain all of your aem instance binaries and their respective repository.  As a convention name project folder accordingly: PROJECT_NAME-AEM_TYPE (e.g. girlscouts-author or girlscouts-publish).  You will need to place three items in this folder to get started:
	- license.properties (with applicable license information)
	- aem jar file named according to aem instance type and port (e.g. cq56-author-4502.jar)
	- startup.sh with java startup parameters for the aem jar file (e.g. java -Xmx1536m -XX:MaxPermSize=384M -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=45022 -verbose:gc -XX:+PrintGCDetails -DauthEnabled=false -jar cq56-author-4502.jar -v -nofork -ll 3)

2. run the start.sh script to start the author server (first time takes a few minutes)

3. browse localhost:4502

4. log in with one of the default user/passwords:
    admin/admin
    author/author

## Pushing and pulling code between CQ and your file system

#### New projects: Adobe developer tools for Eclipse
1. in Eclipse: Help > Install new software > add
    http://eclipse.adobe.com/aem/dev-tools/
2. install everything you find there
3. restart Eclipse
4. in Eclipse: Window > Show View > Other > AEM
5. at bottom left in the Servers tab setup a new AEM Server
6. open [your eclipse workspace]/.metadata/.plugins/org.eclipse.wst.server.core/servers.xml and change the port from 8080 to 4502 also change auto-publish-time="4" and the debugPort to 45022 or the port specified in the start.sh startup script.
7. right click the server and choose Add and Remove... then add your resources to be auto synched
8a. now right click the project area and choose New > Project > AEM > AEM Sample Multi-Module Project
8b. if you already have an existing project, import that project as an existing maven project. Java bundle project will automatically be registered into Eclipse AEM module.  However, content projects will not be registered as there is a bug in AEM's module.  To fix, after importing the content folder, open the .settings for each eclipse subfolder and add the following as org.eclipse.wst.common.project.facet.core.xml, being careful to correctly specify whether it's a "sling.bundle" or a "sling.content" as shown here:

<?xml version="1.0" encoding="UTF-8"?>
<faceted-project>
  <installed facet="sling.content" version="1.0"/>
</faceted-project>

Next, you will have to modify the eclipse .project file entries for buildSpec and natures.  For example:
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>gsusa-content</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.wst.common.project.facet.core.builder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.eclipse.wst.validation.validationbuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.eclipse.m2e.core.maven2Builder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
                <nature>org.eclipse.pde.PluginNature</nature>
		<nature>org.eclipse.jdt.core.javanature</nature>
		<nature>org.eclipse.m2e.core.maven2Nature</nature>
		<nature>org.eclipse.wst.common.project.facet.core.nature</nature>
	</natures>
</projectDescription>

Once you make these edits, you will have to remove the content subproject and re-add it.

9. Right-click each content project (eg gsusa-content) and go to Properties > AEM
		Then fill in content sync root directory to point to your jcr root subfolder (eg src/main/content/jcr_root)

10. Make sure the server at bottom left is running (otherwise right click > start)

Now any changes you make in Eclipse are automatically reflected in AEM.  This is a one-way flow: any changes made directly in AEM will be overwritten
In case you need to pull code the other way, say if you maybe made a new component in cq, then to bring it into Eclipse you can:
1. click Save All in crxde
2. right click the corresponding folder in Eclipse (AEM view) and select Import from server...
    your server must of course be started for it to work (both the CQ server as well as the Eclipse AEM server at bottom left)

#### Troubleshooting Tips (Optional)
1. Install JD Eclipse Realign by adding http://mchr3k-eclipse.appspot.com/ in Help > Install New Software.  Add Java Decompiler Eclipse Plug-in items.
2. Restart Eclipse then go to preferences > Java > Decompiler > JD-Eclipse Realign and check Display line numbers.
3. Create a folder called CQ and place the runtime CQ jar into a folder called src.
4. Expand jar file.
5. Enter expanded folder and further expand static/app/cq-quickstart-5.6.1-standalone.jar
6. Create new project called CQ in Eclipse and point to the the CQ folder.
7. You will now be able to enter any jar file and view the source of the classes with properly aligned line numbers.
8. Go to any open AEM project and open properties.  Set this CQ project as a project reference under Project References.

Alternatively, you can install the latest jd-eclipse plugin (https://github.com/java-decompiler/jd-eclipse.git) but you will need to compi
le it via:
        - cd $jd-eclipse-src-folder; chmod a+x gradlew; ./gradlew installSiteDist; ./gradlew downloadDependencies
        - Help > Install New Software...; "Add..." to add an new repository; Enter "JD-Eclipse Update Site" and select the local site director y (e.g. $jd-eclipse-src-folder/build/install/jd-eclipse-site)
	- Navigate to General -> Editors -> File Associations and use JD Class File Viewer as default editor for *.class and *.class without source

To test debugging, go to SlingHttpServletRequestImpl.class and add a breakpoint in the constructer.  Load your AEM website and this breakpoint should be tripped.

#### Legacy projects: pushing code into CQ
1. the basic maven project structure was auto-generated like this:

     mvn archetype:generate \
        -DarchetypeRepository=http://repo.adobe.com/nexus/content/groups/public/ \
        -DarchetypeGroupId=com.day.jcr.vault \
        -DarchetypeArtifactId=multimodule-content-package-archetype \
        -DarchetypeVersion=1.0.2 \
        -DgroupId=com.northpoint \
        -DartifactId=cqbase \
        -Dversion=1.0-SNAPSHOT \
        -Dpackage=com.northpoint.cqbase \
        -DappsFolderName=cqbase \
        -DartifactName="NorthPoint CQ Base" \
        -DcqVersion="6.0.0" \
        -DpackageGroup="NorthPoint Digital"


    notice the structure of the source code is:
        content: UI resources go here
        bundle: OSGI service code goes here

2. deploy to your running AEM instance:
    cd cqbase
    mvn -PautoInstallPackage install
    mvn -PautoInstallBundle install


    if you look inside pom.xml, bundle/pom.xml, and content/pom.xml you'll see that autoInstallPackage and autoInstallBundle are maven profiles which can be enhanced as needed
    similarly, you could install to a remote server by adding another profile:

        <profiles>
            <!-- ... -->

            <profile>
                <id>integrationServer</id>
                <properties>
                    <crx.host>dev-author.intranet</crx.host>
                    <crx.port>5502</crx.port>
                    <publish.crx.host>dev-publish.intranet</publish.crx.host>
                    <publish.crx.port>5503</publish.crx.port>
                </properties>
            </profile>
        </profiles>

    and invoking like this:
        mvn -PautoInstallPackage -PintegrationServer install

    or you could specify flags on the cmd line:
        -Dcrx.host=otherhost -Dcrx.port=5502

3. see your handiwork by browsing here:


#### Legacy projects: pulling code out from CQ
We just saw how to push code into CQ.  But how about pulling it back out?  Sometimes it's easiest to create a component or template or page directly in CQ, then pull it out into your source control.  For this we need Eclipse + the VaultClipse plugin

initial setup:
1. on the cmd line cd to [your cq server]/crx-quickstart/opt/filevault
2. unzip filevault.zip
3. if it expands to vault-cli-3.* this is NO GOOD!  You'll have to get someone else's zip of vault-cli-2.* and install it in the same spot
4. install the VaultClipse plugin to Eclipse (found in Eclipse > Help > Eclipse Marketplace)
5. now in Eclipse right-click your project, then go to Properties > VaultClipse > Use Workspace Settings > Configure Workspace Settings
    and in Vault Directory put [your cq server]/crx-quickstart/opt/filevault/vault-cli-2.4.34 and hit Apply and OK

    if all else fails you can instead shut down Eclipse and just directly put this in [your eclipse workspace]/.metadata/.plugins/org.eclipse.core.runtime/.settings/com.cp.vaultclipse.prefs:

    eclipse.preferences.version=1
    vault.directory=/[your cq server]/crx-quickstart/opt/filevault/vault-cli-2.4.34

daily usage once it's been setup:
1. make sure you've SAVED your work in crx/de!  If you didn't explicitly hit "Save All" in http://localhost:4502/crx/de then your work isn't saved and won't be pulled in!
2. in Eclipse project explorer, browse to: content/src/main/content/jcr_root/apps/cqbase
3. right-click the cqbase folder and select VaultClipse > Export
4. you should now see your content reflected in your file system, which you can now check into your version control



CQ/AEM reference
================

## Some CQ/AEM terminology
AEM/CQ5 programmers create templates, which in turn are then used by authors (non-programmers) to create actual pages.

The template is like a skeleton defining what elements (called "components") may exist on a page.  Authors (ie non-programmers) then use these templates to pick and choose elements (components) to compose an actual page to be shown in the website

Templates consist of components, which themselves consist of widgets

A component can include other components, and are essentially a collection of scripts (eg JSPs, servlets, etc) that fully realize a specific function

The key property of a template is the "jcr:content/sling:resourceType" which determines how it is rendered. A typical value for this might be [your app name]/components/contentpage



## Overview of jcr/file structure

The basic node structure in jcr of a typical app is like this:

    /
     - apps
       - [your site name]:   custom templates and components (jsps, js, etc)  specific to your website. components can be based on libs/foundation/components
         - components:  website components
           - page:  "page" components
         - templates:  your jsps, html, etc
         - src: OSGi bundle source code is here
         - install: compiled OSGi bundles go here
     - content:   the "data", ie content for your website (dam, user generated, and pages)
     - etc:   utils and tools
       - packages:   packages go here
       - workflow:   workflows go here
     - home:   users and groups
     - libs:   Adobe libs. dont touch!
     - tmp
     - var:   sys files (audit logs, stats, event handling)




## File mapping between your filesystem and AEM:

    Your filesystem path                 |   AEM/CQ5 JCR equivalent path
    ----------------------------------------------------------------------
    content/src/main/content/jcr_root/   |   /



## Main admin urls
The CQ Welcome screen.
    http://localhost:4052/libs/cq/core/content/welcome.html

The CRX administration Console.
    http://localhost:4052/crx/explorer

Package Manager
     http://localhost:4052/crx/packmgr

The CRXDE Lite console.
    http://localhost:4052/crx/de

The Apache Felix Web Console.
    http://localhost:4052/system/console

User Administration
    http://localhost:4052/useradmin

Sling Resource Resolver config
    http://localhost:4502/system/console/jcrresolver



## Common gotchas
- Don't use VLT for source control.  Better yet, don't use it at all
- in CRX/DE Lite, if your view looks odd you're probably logged out and seeing a "guest" view.  Click the top right corner to log in again
- Any changes made in the CRX/DE Lite view are not saved until you explicitly hit Save All in the top left
- The "refresh" icon in CRX/DE Lite doesn't always work properly.  Use your browser's refresh instead


## Online help
http://docs.adobe.com/docs/en/aem/6-0/develop/dev-tools/ht-projects-maven.html


## Dispatcher Download
https://www.adobeaemcloud.com/content/companies/public/adobe/dispatcher/dispatcher.html

## deploying to DEV environment
git branch "root" is the latest n greatest, while "master" is what's in prod

there are 3 sites: gsusa, vtk, and web

To deploy vtk to DEV:

cd web; mvn clean install
cd vtk; mvn clean install
./deployDev.sh
(if needed) flush the dispatcher cache:
	ssh npUser@54.83.199.114
	sudo ./flushCache

see your handiwork at https://girlscouts-dev2.adobecqms.net/content/girlscouts-vtk/en/vtk.home.html
