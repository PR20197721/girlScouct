import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*

def root = "/content/";
def oldResourceType = "girlscouts/components/advertisement";
def newResourceType = "girlscouts/components/right-rail-banners";

//get all councilPaths
List<String> counsilList = new ArrayList<String>();
    getNode(root).recurse {rootNode ->
            if (rootNode.hasProperty('adsPath')) {
                counsilList.add(rootNode.getParent().getPath());
            }
    }

for (String item : counsilList) {
    execute(item,oldResourceType,newResourceType);
}

def execute(cPath,oRType,nRtype){
    updateResourceType(cPath,oRType,nRtype);
    updateAdNode(cPath);
    updateDesignAdnode(cPath,oRType,nRtype);
    updateAdPageName(cPath);
}

//updates advertisement resourceType to right-rail-banners
def updateResourceType(cPath,oRType,nRtype){
    println "executing updateResourceType() for"+ cPath;
    Resource resource= resourceResolver.getResource(cPath);
    if(null != resource ){
        getNode(cPath).recurse{cNode ->
                if(cNode.hasProperty('sling:resourceType')){
                    final def resourceType = cNode.getProperty('sling:resourceType').string
                    if (resourceType.equals(oRType)) {
                            println "changing " + cNode.path
                            cNode.setProperty('sling:resourceType', nRtype)
                            cNode.save();
                        }
                }

            }
    }   

}

//udates advertisement node in council content.
def updateAdNode(cPath){
    println "executing updateAdNode() for"+ cPath;
    Resource resource= resourceResolver.getResource(cPath);
    if(null != resource){
        getNode(cPath).recurse {cNode ->
                if (cNode.hasNode('advertisement')) {
                    final def adNodeName = cNode.getNode('advertisement').getName()
                    if (adNodeName.equals('advertisement')) {                        
                        cNode.getSession().move(cNode.getPath() + "/" + "advertisement", cNode.getPath() + "/" + 'right-rail-banners')
                        println("advertisment Node :"+ cNode.getPath())
                        cNode.save()
                    }
                }
        }
    }    

}

//updates advertisement node of council design
def updateDesignAdnode(cPath,oRType,nRtype){
println "executing updateDesignAdnode() for"+ cPath;
    getNode(cPath).recurse{cNode->
        if(cNode.hasProperty('cq:designPath')){
            final def councilDesignPath = cNode.getProperty('cq:designPath').string                                   
            updateResourceType(councilDesignPath,oRType,nRtype);
            updateAdNode(councilDesignPath)
        }
    }

}

//Renames the Ad-Page in council site.
def updateAdPageName(cPath){
    println "executing updateAdPageName() for"+ cPath;
    final def adPage;
    def rfList = [];
    getNode(cPath).recurse{cNode ->
        if(cNode.hasNode('ad-page')){
            adPage = cNode.getNode('ad-page').getPath();
        }
    }
        if(null != adPage){
            def query = getReferences(cPath, adPage)
            def result = query.execute()
            result.nodes.each {node -> rfList.add(node.path)
            println("Reference found at :" + node.path)
        }
        def refList = rfList as String[];
        final def newAdPage = adPage.replaceAll("ad-page", "right-rails");
        Page page = resourceResolver.getResource(adPage).adaptTo(Page.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        pageManager.move(page, newAdPage, null, false, false, refList);
    }
    
}


def getReferences(cPath, aPath) {
    println("In getReferences()")
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${cPath}]) and contains (s.*,'${aPath}')"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    query
}
