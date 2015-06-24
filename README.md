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
1. run the start.sh script to start the author server (first time takes a few minutes)
    look inside that script, it's really just a one liner:
    java -jar -Xmx1500M -XX:MaxPermSize=512M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=4508 cq-author-p4502.jar

   notice how the name of the jar itself determines the port it runs on (here it is port 4502):

2. browse localhost:4502

3. log in with one of the default user/passwords:
    admin/admin
    author/author



## Pushing and pulling code btwn CQ and your file system

#### New projects: Adobe developer tools for Eclipse
1. in Eclipse: Help > Install new software > add 
    http://eclipse.adobe.com/aem/dev-tools/
2. install everything you find there
3. open [your eclipse workspace]/.metadata/.plugins/org.eclipse.wst.server.core/servers.xml and change the port from 8080 to 4502
    also change auto-publish-time="4"
4. restart Eclipse
5. in Eclipse: Window > Show View > Other > AEM
6. at bottom left in the Servers tab setup a new AEM Server
7. right click the server and choose Add and Remove... then add your resources to be auto synched
8. now right click the project area and choose New > Project > AEM > AEM Sample Multi-Module Project
9. Make sure the server at bottom left is running (otherwise right click > start)

Now any changes you make in Eclipse are automatically reflected in AEM.  This is a one-way flow: any changes made directly in AEM will be overwritten
In case you need to pull code the other way, say if you maybe made a new component in cq, then to bring it into Eclipse you can:
1. click Save All in crxde
2. right click the corresponding folder in Eclipse (AEM view) and select Import from server... 
    your server must of course be started for it to work (both the CQ server as well as the Eclipse AEM server at bottom left)


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

