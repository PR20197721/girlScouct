<%@ page import="java.util.StringTokenizer,
				java.util.Set,
				java.util.HashSet,
				java.lang.StringBuilder,
				javax.jcr.Node,
				javax.jcr.NodeIterator" %>
<%!
String getFormId(Node thisNode) {
    try {
		Node parentNode = thisNode.getParent();
		NodeIterator iter = parentNode.getNodes();
		String formId = null;
		while (iter.hasNext()) {
		    Node node = iter.nextNode();
		    if (node.hasProperty("sling:resourceType")) {
		        String resourceType = node.getProperty("sling:resourceType").getString();
		        if (resourceType.equals("foundation/components/form/start")) {
		            formId = node.getProperty("formid").getString();
		        }
		        if (node.getPath().equals(thisNode.getPath())) {
		            // FormId for this node found
		            break;
		        }
		    }
		}
		return formId;
    } catch (Exception e) {
        return null;
    }
}

class FormatExpressionResult {
	String expression;
	Set<String> fields;
}

FormatExpressionResult formatExpression(String expression, String formId) {
	String DELIMS = "+-*/()";
	
	StringBuilder builder = new StringBuilder();
	Set<String> fields = new HashSet<String>();
	StringTokenizer tokenizer = new StringTokenizer(expression, "[" + DELIMS + "]", true);
	while (tokenizer.hasMoreTokens()) {
	    String token = tokenizer.nextToken();
	   	if (DELIMS.indexOf(token) == -1) {
	   	    // it is a field
	   	    builder.append("Number($('form#"+ formId)
	   	    	.append(" input[name=" + token + "]').val())");
	   	    fields.add(token);
	   	} else {
	   	    // it is an operator
	   	    builder.append(token);
	   	}
	}

	FormatExpressionResult result = new FormatExpressionResult();
	result.expression = builder.toString();
	result.fields = fields;
	return result;
}
%>