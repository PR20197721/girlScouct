import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import java.util.List
import java.util.ArrayList


String QUERY_LANGUAGE = "JCR-SQL2";
String EXPRESSION = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([/etc/designs]) and s.[components]='group:Girl Scouts Form'";

QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)
List<String> replacementList = new ArrayList<String>();
replacementList.add("foundation/components/form/dropdown-no-edit");
replacementList.add("foundation/components/form/radio");
replacementList.add("foundation/components/form/password");
replacementList.add("foundation/components/form/submit");
replacementList.add("foundation/components/form/start");
replacementList.add("foundation/components/form/upload");
replacementList.add("foundation/components/form/checkbox");
replacementList.add("foundation/components/form/calculation");
replacementList.add("foundation/components/form/imagebutton");
replacementList.add("foundation/components/form/address");
replacementList.add("foundation/components/form/dropdown");
replacementList.add("foundation/components/form/hidden");
replacementList.add("girlscouts/components/form/text");
replacementList.add("girlscouts/components/form/captcha");
if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			try {
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				Property componentsProp = node.getProperty("components");
				if(componentsProp.isMultiple()){
					Value[]	componentsVals = componentsProp.getValues();
					List<String> newComponentVals = new ArrayList<String>();
					println("Before swap:   "+componentsVals)
					for(Value componentsVal:componentsVals){
						if("group:Girl Scouts Form".equals(componentsVal.getString())){
							println("   swap: "+componentsVal)
							newComponentVals.addAll(replacementList);
						}else{
							println("   noswap: "+componentsVal)
							newComponentVals.add(componentsVal.getString());
						}
					}
					componentsProp.setValue(newComponentVals.toArray(new String[newComponentVals.size()]));
				}else{
					componentsProp.setValue(replacementList.toArray(new String[replacementList.size()]));
				}
				println("After swap:   "+componentsProp.getValues())
				save();
			} catch (Exception e) {
				println(e.getMessage())
				e.printStackTrace()
			}
		}
	} catch (Exception e) {
		println(e.getMessage())
		e.printStackTrace()
	}
}

def QueryResult search(EXPRESSION, QUERY_LANGUAGE) {
	println(EXPRESSION)
	QueryResult result = null;
	try {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		Query sql2Query = queryManager.createQuery(EXPRESSION, QUERY_LANGUAGE);
		return sql2Query.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}