#!/usr/bin/python
# Migrates the data
# prod => stage => dev => local
# Usage ./data-migrate [target-env]
# e.g. ./data-migrate stage
# Migrate data from prod auth to stage auth 
# and then replicate tree to stage pub

import sys, os

all_conf = {
    'local' : {
        'auth_url' : 'localhost:4502',
        'pub_url' : 'localhost:4503',
        'username' : 'admin',
        'password' : 'admin',
        'admin_password' : 'admin',
        'source' : 'dev'
    },
    'dev' : {
        'auth_url' : '54.83.199.117:4502',
        'pub_url' : '54.83.199.118:4503',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'admin_password' : '@Q&W1iHzx(',
        'source' : 'stage'
    },
    'stage' : {
        'auth_url' : '54.86.13.38:4502',
        'pub_url' : '54.85.69.30:4503',
        'username' : 'datamigrate',
        'password' : 'datamigrate123',
        'admin_password' : '4U5Hsq5Q_I',
        'source' : 'prod'
    },
    'prod' : {
        'auth_url' : '54.84.115.158:4502',
        'pub_url' : '54.85.131.182:4503',
        'username' : '####off',
        'password' : '####off'
    }
}

branches_auth=[
    '/content/dam/girlscouts-vtk'
]

branches_pub=[
    '/vtk'
]

batch_size = 1024

def switch_component(name, switch):
    "Switch a component on/off"
    switch_str = "disable"
    if switch:
        switch_str = "enable"
    os.system("curl -silent -u admin:" + dst_conf['admin_password'] + " --data 'action=" + switch_str + "' http://" + dst_conf['auth_url'] + "/system/console/components/" + name + "/" + name + " > /dev/null") # due to wierd Felix REST API, component has to be repeated twice.
    print "### " + switch_str + "d component: " + name;

def rcp(branch, runmode):
    "RCP data. Runmode can be auth/pub"
    os.system("vlt rcp -b " + str(batch_size) + " -r -u -n http://" + src_conf['username'] + ":" + src_conf['password'] + "@" + src_conf[runmode + '_url'] + "/crx/-/jcr:root" + branch + " http://" + dst_conf["username"] + ":" + dst_conf["password"] + "@" + dst_conf[runmode + '_url'] + "/crx/-/jcr:root" + branch);
    print "### Migrated branch: " + branch

####################
# Main
####################
if len(sys.argv) < 2:
    sys.exit('Please provide target env: local dev stage');

dst_env = sys.argv[1]
dst_conf = all_conf[dst_env]
src_env = dst_conf['source']
src_conf = all_conf[src_env]

# Stop workflow launcher and listener to prevent DAM workflows
switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl', False);
switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener', False);

for branch in branches_auth:
    rcp(branch, 'auth');
    
# Re-enable workflow launcher and listener
switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl', True);
switch_component('com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener', True);
