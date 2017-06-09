The order of listings must be preserved. The rules at the bottom receive higher priority

/content – there are a lot of junk listings here. The ones believed to be necessary are listed

Principal	Path		Allow/Deny	Privileges	Restrictions
gs-reviewers	/content	Deny		jcr:read	rep:glob=/*
gs-authors	/content	Deny		jcr:read	rep:glob=/*
everyone	/content	Deny		jcr:all		rep:ntNames=cq:meta


/content/dam

Principal		Path		Allow/Deny	Privileges
content-analysts	/content/dam	Allow		crx:replicate, jcr:lockManagement, jcr:versionManagement, rep:write	
gs-authors		/content/dam	Allow		jcr:read	
gs-reviewers		/content/dam	Allow		jcr:read	


/etc

Principal		Path	Allow/Deny	Privileges			Restrictions
everyone		/etc	Deny		jcr:all	
gs-reviewers		/etc	Allow		jcr:read, jcr:removeChildNodes*	
content-analysts	/etc	Allow		jcr:read, jcr:removeChildNodes*	
gs-authors		/etc	Allow		jcr:read, jcr:removeChildNodes*	
everyone		/etc	Deny		jcr:all				rep:glob=/*
administrators		/etc	Allow		jcr:all	

/etc/tags

Principal	Path		Allow/Deny	Privileges			Restrictions
everyone	/etc/tags	Deny		jcr:all	
gs-reviewers	/etc/tags	Allow		jcr:read, jcr:removeChildNodes*	
gs-authors	/etc/tags	Allow		jcr:read, jcr:removeChildNodes*	
everyone	/etc/tags	Deny		jcr:all				rep:glob=/*
administrators	/etc/tags	Allow		jcr:all	


/etc/scaffolding

Principal		Path			Allow/Deny	Privileges
everyone		/etc/scaffolding	Deny		jcr:all	
gs-reviewers		/etc/scaffolding	Allow		jcr:read, jcr:removeChildNodes*	
gs-authors		/etc/scaffolding	Allow		jcr:read, jcr:removeChildNodes*	
content-analysts	/etc/scaffolding	Allow		jcr:read	
administrators		/etc/scaffolding	Allow		jcr:all	

Note: In scaffolding, we were unable to make the permissions such that the gs-reviewers and gs-authors base groups could have permission to the directory, but not the children. When new councils are created, an automated process denies permissions to the child nodes. It seems to be an issue with AEM that does not exist for tags

/etc/designs

Principal		Path		Allow/Deny	Privileges			Restrictions
everyone		/etc/designs	Deny		jcr:all	
gs-reviewers		/etc/designs	Allow		jcr:read, jcr:removeChildNodes*	
gs-authors		/etc/designs	Allow		jcr:read, jcr:removeChildNodes*	
everyone		/etc/designs	Deny		jcr:all				rep:glob=/*
content-analysts	/etc/designs	Allow		jcr:read	
administrators		/etc/designs	Allow		jcr:all	

* Believed to be unnecessary

