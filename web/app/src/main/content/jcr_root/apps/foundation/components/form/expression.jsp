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

boolean isNumber(String input) {
	try {
		Double.parseDouble(input);
	} catch (NumberFormatException e) {
		return false;
	}
	return true;
}

FormatExpressionResult formatExpression(String expression, String formId) {
	String DELIMS = "+-*/()=><!";
	
	StringBuilder builder = new StringBuilder();
	Set<String> fields = new HashSet<String>();
	StringTokenizer tokenizer = new StringTokenizer(expression, "[" + DELIMS + "]", true);
	String prevToken = "";
	while (tokenizer.hasMoreTokens()) {
	    String token = tokenizer.nextToken();
	   	if (DELIMS.indexOf(token) == -1) {
	   	    if (isNumber(token)) {
	   	        // it is a number
	   	        builder.append(token);
	   	    } else {
		   	    // it is a field
		   	    builder.append("Number($('form#"+ formId)
		   	    	.append(" input[name=" + token + "]').val())");
		   	    fields.add(token);
	   	    }
	   	} else {
	   	    // it is an operator
	   	    if (token.equals("=") && 
	   	            !prevToken.equals("<") && 
	   	            !prevToken.equals(">")) { // in case >= <=
	   	        builder.append("==");
	   	    } else {
   	    		builder.append(token);
	   	    }
	   	}
	   	prevToken = token;
	}

	FormatExpressionResult result = new FormatExpressionResult();
	result.expression = builder.toString();
	result.fields = fields;
	return result;
}
%>