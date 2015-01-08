#!/usr/bin/python
# Migrates the data
# prod => stage => dev => local
# Usage ./data-migrate [target-env]
# e.g. ./data-migrate stage
# Migrate data from prod auth to stage auth 
# then migrate data from stage auth to stage pub
# finally do some cleanup

import sys, os, time

start_time = time.time()

all_conf = {
    'local-auth' : {
        'url' : 'localhost:4502',
        'username' : 'admin',
        'password' : 'admin',
        'admin_password' : 'admin',
        'source' : 'dev-auth'
    },
    'local-pub' : {
        'url' : 'localhost:4503',
        'username' : 'admin',
        'password' : 'admin',
        'source' : 'local-auth'
    },
    'dev-auth' : {
        'url' : '54.83.199.117:4502',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'admin_password' : '@Q&W1iHzx(',
        'source' : 'stage-auth'
    },
    'dev-pub' : {
        'url' : '54.83.199.118:4503',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'source' : 'dev-auth'
    },
    'stage-auth' : {
        'url' : '54.86.13.38:4502',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'admin_password' : '4U5Hsq5Q_I',
        'source' : 'prod-auth'
    },
    'stage-pub' : {
        'url' : '54.85.69.30:4503',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'source' : 'stage-auth'
    },
    'prod-auth' : {
        'url' : '54.84.115.158:4502',
        'username' : 'proddatamigrate',
        'password' : 'v3swezaz'
    }
}

branches=[
    '/content'
]

exclude='-e "/content/catalogs/.*" -e "/content/campaigns/.*" -e "/content/launches/.*" -e "/content/usergenerated/.*" -e "/content/publications/.*" -e "/content/communities/.*" -e "/content/mac/.*" -e "/vtk/global-settings" -e "/content/dam/hierarchy/.*"'

MIGRATE_TOOL_JAR = 'migrate-tools-1.0-SNAPSHOT.one-jar.jar'

batch_size = 1024

def switch_component(name, switch):
    "Switch a component on/off"
    switch_str = "disable"
    if switch:
        switch_str = "enable"
    os.system("curl -silent -u 'admin:" + dst_conf['admin_password'] + "' --data 'action=" + switch_str + "' http://localhost:4502/system/console/components/" + name + "/" + name + " > /dev/null") # due to wierd Felix REST API, component has to be repeated twice.
    print "### " + switch_str + "d component: " + name;

def rcp(branch):
    "RCP data."
    os.system("vlt rcp -b " + str(batch_size) + " " + exclude + " -r -u -n http://" + src_conf['username'] + ":" + src_conf['password'] + "@" + src_conf['url'] + "/crx/-/jcr:root" + branch + " http://" + dst_conf["username"] + ":" + dst_conf["password"] + "@localhost:" + port + "/crx/-/jcr:root" + branch);
    print "### Migrated branch: " + branch

####################
# Main
####################
if len(sys.argv) < 2:
    sys.exit('Please provide target env: local-auth local-pub dev-auth dev-pub stage-auth stage-pub');

dst_env = sys.argv[1]
runmode = dst_env.split('-')[1]
dst_conf = all_conf[dst_env]
src_env = dst_conf['source']
src_conf = all_conf[src_env]
port = '4502'
if runmode == 'pub':
    port = '4503'

# Stop workflow launcher and listener to prevent DAM workflows
if runmode == 'auth':
    switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl', False);
    switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener', False);

for branch in branches:
    rcp(branch)

# Re-enable workflow launcher and listener
if runmode == 'auth':
    switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl', True);
    switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener', True);

migrate_tool = 'java -jar ' + MIGRATE_TOOL_JAR + ' http://' + dst_conf['url'] + '/crx/server ' + dst_conf['username'] + ' ' + dst_conf['password'] + ' '
if runmode == 'pub':
    # Remove deactivated nodes
    for branch in branches:
        os.system(migrate_tool + 'removedeactivated ' + branch)     

if dst_env.split('-')[0] == 'stage':
    # Update VTK link 
    for branch in branches:
        os.system(migrate_tool + 'updatelink ' + branch)     

elapsed_time = time.time() - start_time
print "All Done. Time elapsed: " + str(elapsed_time) + " seconds."
