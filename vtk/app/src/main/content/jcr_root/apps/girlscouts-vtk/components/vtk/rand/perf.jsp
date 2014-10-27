<%@page import="org.apache.jackrabbit.commons.flat.BTreeManager" %>
<% 
// Create a new TreeManager instance rooted at node. Splitting of nodes takes place
 // when the number of children of a node exceeds 40 and is done such that each new
 // node has at least 40 child nodes. The keys are ordered according to the natural
 // order of java.lang.String.
 org.apache.jackrabbit.commons.flat.TreeManager treeManager = new org.apache.jackrabbit.commons.flat.BTreeManager(node, 20, 40, Rank.<String>comparableComparator(), true);

 // Create a new NodeSequence with that tree manager
 NodeSequence nodes = ItemSequence.createNodeSequence(treeManager);

 // Add nodes with key "jcr" and "day"
 nodes.addNode("jcr", NodeType.NT_UNSTRUCTURED);
 nodes.addNode("day", NodeType.NT_UNSTRUCTURED);

 // Iterate over the node in the sequence.
 // Prints "day jcr "
 for (Node n : nodes) {
     System.out.print(n.getName() + " ");
 }

 // Retrieve node with key "jcr"
 Node n = nodes.getItem("jcr");

 // Remove node with key "day"
 nodes.removeNode("day");
 %>