package my.test;

import java.util.*;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;

public class Fmt {

	public static void main(String []args){
		
try{	
 	
		// Connection
        Repository repository = JcrUtils.getRepository("http://localhost:4503/crx/server/");
        
        //Workspace Login
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = null;       
        session = repository.login(creds, "crx.default"); 
        try{
   	
        	Node vtkRootNode = session.getNode("/vtk");
    		String sql="select * from nt:unstructured where jcr:path like '/vtk/%/users/%'";
    		
    		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
    		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
    		q.setLimit(1);
    		QueryResult result = q.execute();
       for (RowIterator it = result.getRows(); it.hasNext(); ) {
    	   
    	   
           Row r = it.nextRow();
          
           Value excerpt = r.getValue("jcr:path");
           
           StringTokenizer t = new StringTokenizer( excerpt.getString(), "/");
           String vtk = t.nextToken();
           String council = t.nextToken();
           String troop = t.nextToken();
           t.nextToken();
           String user = t.nextToken();
           
           String from ="/vtk/"+council+"/"+ troop+"/users/"+user;
           String to= "/vtk/"+ council+"/troops/"+troop ;
           String to1 = "/vtk/"+ council+"/troops";
           
        
           Node x =JcrUtils.getOrCreateByPath(to1, "nt:unstructured", session);
           session.move(from, to);
           Node newTroop = session.getNode(to);
           newTroop.setProperty("ocm_classname" , "org.girlscouts.vtk.models.Troop");
           session.save();
           
           if(true)return;
           
       }
        }catch(Exception e){e.printStackTrace();}

}catch(Exception e){e.printStackTrace();}
}
	
	
}
