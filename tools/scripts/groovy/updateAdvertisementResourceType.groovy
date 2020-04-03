import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*

def root = "/content/";
def oldResourceType = "girlscouts/components/advertisement";
def newResourceType = "girlscouts/components/right-rail-banners";

List <String> counsilList = new ArrayList <String> ();
println "=========================================="
println "Execution started :"+ new Date();
println "=========================================="
counsilList = getCounsilList(root);

for (String item: counsilList) {
    updateResourceType(item, oldResourceType, newResourceType);
    updateDesignAdnode(item, oldResourceType, newResourceType);
    updateAdPageName(item);
}
println "=========================================="
println "Execution Stopped :"+ new Date();
println "=========================================="

//Move ad-page to right-rails
def updateAdPageName(cPath){
    println "Updating ad-page name for " + cPath;
    def stmt = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${cPath}]) and s.[adsPath] is not "+"NULL";
    def result = queryResults(stmt);
    def rfList = [];
    try{
        if(null != result){
            def rowIter = result.getRows();
            while(rowIter.hasNext()){
                def row = rowIter.nextRow();
                node = row.getNode();
                if(node.hasProperty('adsPath')){
                    def adPage = node.getProperty("adsPath").getString();                    
                    if(null != adPage){
                        def query = getReferences(cPath, adPage)
                        def resultSet = query.execute()
                        resultSet.nodes.each {node -> rfList.add(node.path)
                        println("Reference found at :" + node.path)
                        }
                    }
                    def refList = rfList as String[];
                    final def newAdPage = adPage.replaceAll("ad-page", "right-rails");
                    Page page = resourceResolver.getResource(adPage).adaptTo(Page.class);
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    pageManager.move(page, newAdPage, null, false, false, refList);
                    sleep(1000);
                }
            }
        }
    }catch(Exception e){
        println "Exception occured at updateAdPageName() "+e;
    }
}
//Finds references of ad-page
def getReferences(cPath, aPath) {    
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${cPath}]) and contains (s.*,'${aPath}')"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    query
}

//updates advertisement node of council design
def updateDesignAdnode(cPath, oRType, nRtype) {
    println "Updating Resource Type and Advertisment Node for " + cPath;
    def stmt = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${cPath}]) and s.[cq:designPath] is not "+"NULL";
    def result = queryResults(stmt);
    println "Number of results found: "+result.getRows().size();
    try{
        if(null != result){
            def rowIter = result.getRows();
            while (rowIter.hasNext()) {
                def row = rowIter.nextRow();
                node = row.getNode();
                if(node.hasProperty('cq:designPath')){
                    def councilDesignPath = node.getProperty("cq:designPath").getString();                    
                    updateResourceType(councilDesignPath,oRType,nRtype);                    
                 }
            }
        }
    }catch(Exception e){
        println "Exception occured in updateDesignNode"+e;
    }
   
}

//updates advertisement resourceType to right-rail-banners
def updateResourceType(cPath, oRType, nRtype) {
    println "Updating ResourcetType and Advertisement node for " + cPath;
    def stmt = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${cPath}]) and s.[sling:resourceType] = '${oRType}'";
    Node node = null;
    def result = queryResults(stmt);
   println "Number of results found: "+result.getRows().size();
    try {
        if (result != null) {
            def rowIter = result.getRows();
            while (rowIter.hasNext()) {
                def row = rowIter.nextRow();
                node = row.getNode();
                def resVal = node.getProperty("sling:resourceType").getString();
                if (resVal.equals(oRType)) {
                    node.setProperty('sling:resourceType', nRtype);                    
                }
                def adNodeName = node.getName();
                if(adNodeName.equals('advertisement')){
                    node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + 'right-rail-banners');                    
                }
            }
            if(null != node){
                node.save();
            }
            sleep(1000);
        }
    } catch (Exception e) {
        println("Exception occured at updateResourceType() :"+e);        
    }
}

def queryResults(statement) {
    def result = null;
    try {
        def queryManager = session.workspace.queryManager;
        def query = queryManager.createQuery(statement, "JCR-SQL2");
        result = query.execute();
    } catch (Exception e) {
        println "Exception occured at queryResults() "+ e;
    }
    return result;
}

//Retrieves total councils
def getCounsilList(String rootPath) {
    Resource parentResource = resourceResolver.getResource(rootPath);
    Iterator <Resource> resources = parentResource.listChildren();
    List <String> councils = new ArrayList <String> ();
    while (resources.hasNext()) {
        Resource res = resources.next();
        if (res.adaptTo(Page.class) != null) {
            councils.add(res.getPath());
        }
    }    
    println "List of Councils :" + councils
    councils;
}
