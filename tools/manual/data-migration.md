Data Migration Tool Manual
==========================

1. Overview
-----------
This tool migrates content data from an upstream server to a downstream server, like from production to staging. The tool has two parts: the java part and the script. The script is written in Python and its source code is located here: tools/scripts/data-migrate.py. The Java part is a standalone Java application and its source code is located here: tools/java/migrate-tools. The script leverages VLT, the data migration tool from Adobe AEM, to migrate the data and the Java part to do data adjustments. 

The tool is installed on the downstream authoring instance. It first migrates data from the upstream authoring instance and then migrates data from itself to the publishing instance.

2. Setup
--------
First, deploy VLT to the server. VLT is in <AEM_HOME>/crx-quickstart/opt/filevault/filevault.zip. Unzip it to an arbitrary directory and add the `bin` sub-directory to `PATH`.
Then, setup the JAVA_HOME variable to make VLT work. e.g. if
`which java`
returns
`/usr/java/latest/bin/java`
we should put this line in the startup script:
`export JAVA_HOME=/usr/java/latest`

3. Usage
--------
To execute the data migration, simply log onto the downstream authoring server and run:  
`./data-migrate.py <env>`  
env can be: local, dev, stage

e.g., on a staging authoring instance, you want to execute:  
`./data-migrate.py stage`  
to migrate data from production to staging.

There might be exceptions during the data migration procedure, and two of them are common: *VersionException* and *LockException*.
If *VersionException* happens, a "checkout" procudure is needed:  
`java -jar migrate-tools-1.0-SNAPSHOT.one-jar.jar http://<server>:<port>/crx/server <username> <password> checkout <node_path>`

If *LockException* happens, an "unlock" procudure is needed:  
`java -jar migrate-tools-1.0-SNAPSHOT.one-jar.jar http://<server>:<port>/crx/server <username> <password> unlock <node_path>`

4. "screen"
-----------
"screen" is recommended to be used to run the data migration procedure because the procedure takes a long time, usually one and a half hours and connection to the server might be lost during this period. "screen" creates a separate session on the server side so even a connection is lost, this session is still running and can be re-attached when the connection is recovered.

To create a new server-side session with screen:  
`screen -S <session_name>`  
To detach from the current session, press:  
CTRL+A+Z  
To see all the sessions:  
`screen -ls`  
To re-attach to a session:  
`screen -r <session_name>`

