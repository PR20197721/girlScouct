import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
oldSlingResourceType = 'girlscouts/components/form/captcha';
newSlingResourceType = 'girlscouts/components/form/recaptcha-v2';
actionType = 'girlscouts/components/form/actions/web-to-case';
propertyMap = [
    'constraintMessage': 'Invalid captcha.',
    'required': 'true',
    'sling:resourceSuperType': 'foundation/components/form/defaults/field',
    'sling:resourceType': newSlingResourceType
];

// Filter
filters = [
    dryRun: true
];

// Results
buildJSON = [
    print: true,
    nodes: true,
    hitCount: true,
    filters: true
];

// For queries run on author, please ensure GSPropertyIndex doesn't have actionType like on publisher
// so that ntBaseLucene can return both actionType and sling:resourceType properties

//*
//*
//* CHANGE EXISTING NODES

// http://34.203.143.115:4503/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2F%0D%0Atype%3Dnt%3Aunstructured%0D%0A1_property%3D..%2F*%2FactionType%0D%0A1_property.value%3Dgirlscouts%2Fcomponents%2Fform%2Factions%2Fweb-to-case%0D%0A2_property%3Dsling%3AresourceType%0D%0A2_property.1_value%3Dgirlscouts%2Fcomponents%2Fform%2Frecaptcha-v2%0D%0A2_property.2_value%3Dgirlscouts%2Fcomponents%2Fform%2Fcaptcha%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildEditQuery(data) {
    Map map = [:];

    map.put('path', '/content/');
    map.put('type', 'nt:unstructured');
    map.put('1_property', 'sling:resourceType');
    map.put('1_property.value', oldSlingResourceType);
    map.put('2_property', '../*/actionType'); // Has a sibling node with actionType property, form start
    map.put('2_property.value', actionType);
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildEditQuery(filters);
editedResult = query.execute();

editedNodes = [];
editedResult.nodes.each { node ->
    // Get node data and set new property
    def nodeObj = new TreeMap();
    nodeObj.put('path', node.path);

    // Change property in node and save
    propertyMap.each { property, value ->
        def oldVal = node.get(property);
        if (oldVal == value) return;
        nodeObj.put('old_' + property, oldVal);
        node.set(property, value);
        nodeObj.put('new_' + property, node.get(property));
    }
    if (nodeObj.size() > 1) {
        editedNodes.add(nodeObj);
        if (!filters.dryRun) {
            session.save();
        }
    }
}

//*
//*
//* ADD MISSING NODES

// http://34.203.143.115:4503/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2F%0D%0Atype%3Dnt%3Aunstructured%0D%0A1_property%3D..%2F*%2FactionType%0D%0A1_property.value%3Dgirlscouts%2Fcomponents%2Fform%2Factions%2Fweb-to-case%0D%0A2_property%3Dsling%3AresourceType%0D%0A2_property.value%3Dfoundation%2Fcomponents%2Fform%2Fsubmit%0D%0Agroup.p.not%3Dtrue%0D%0Agroup.3_property%3D..%2F*%2Fsling%3AresourceType%0D%0Agroup.3_property.1_value%3Dgirlscouts%2Fcomponents%2Fform%2Frecaptcha-v2%0D%0Agroup.3_property.2_value%3Dgirlscouts%2Fcomponents%2Fform%2Fcaptcha%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildCreateQuery(data) {
    Map map = [:];

    map.put('path', '/content/');
    map.put('type', 'nt:unstructured');
    map.put('1_property', 'sling:resourceType');
    map.put('1_property.value', 'foundation/components/form/submit');
    map.put('2_property', '../*/actionType'); // Has a sibling node with actionType property, form start
    map.put('2_property.value', actionType);
    map.put('group.p.not', 'true');
    map.put('group.3_property', '../*/sling:resourceType'); // Does not have a sibling node with a captcha resourceType
    map.put('group.3_property.1_value', oldSlingResourceType);
    map.put('group.3_property.2_value', newSlingResourceType);
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildCreateQuery(filters);
createdResult = query.execute();

createdNodes = [];
createdResult.nodes.each { submitNode ->
    // Add new captcha node to parent
    def node = submitNode.parent.addNode('recaptcha_v2', 'nt:unstructured');

    // Reorder node
    // https://docs.adobe.com/docs/en/spec/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html#orderBefore(java.lang.String,%20java.lang.String)
    // https://www.codota.com/code/java/methods/javax.jcr.Node/orderBefore?snippet=5ce6bef837d9ff0004ebb811
    submitNode.parent.orderBefore(node.name, submitNode.name);

    // Add properties to node
    def nodeObj = new TreeMap();
    nodeObj.put('path', node.path);
    propertyMap.each { property, value ->
        node.set(property, value);
        nodeObj.put(property, value);
    }

    // Save
    createdNodes.add(nodeObj);
    if (!filters.dryRun) {
        session.save();
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.nodes) {
    jsonMap.put('editedNodes', editedNodes);
    jsonMap.put('createdNodes', createdNodes);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numNodesQueried', editedResult.nodes.size() + createdResult.nodes.size());
        dataMap.put('numNodesEdited', editedNodes.size());
        dataMap.put('numNodesCreated', createdNodes.size());
    }
    if (buildJSON.filters) {
        dataMap.put('filters', filters);
    }
    jsonMap.put('_data', dataMap);
}
json.call(jsonMap);
if (buildJSON.print) {
    println json.toPrettyString();
}
