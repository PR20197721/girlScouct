import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
delimeter = '&&';
nodePropertyMap = [
    'url': [
        'vtype': 'vanityPathValidator',
        'fieldDescription': 'Enter the short/vanity URL (e.g. for http://www.girlscouts.org/camp enter /content/gsusa/camp). Vanity paths not ending in a file extension (e.g. pdf) must be completely lower case.'
    ],
    'target': [
        'name': './jcr:content/cq:redirectTarget'
    ],
    'res': [
        'value': 'gsusa/components/redirect&&girlscouts/components/redirect'
    ]
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

//*
//*
//* QUERY

// https://author-uat.girlscouts.org/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fetc%2Fscaffolding%2F*%2Fpermanent-redirect%2Fjcr%3Acontent%2Fdialog%2Fitems%2Fitems%2Ftab1%2F%0D%0Apath.exact%3Dtrue%0D%0Atype%3Dnt%3Aunstructured%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
// http://35.170.150.201:4502/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fetc%2Fscaffolding%2F*%2Fpermanent-redirect%2Fjcr%3Acontent%2Fdialog%2Fitems%2Fitems%2Ftab1%2Fitems%0D%0Apath.exact%3Dtrue%0D%0Atype%3Dnt%3Aunstructured%0D%0Anodename%3Dres%0D%0Aproperty%3Dvalue%0D%0Aproperty.1_value%3Dgsusa%2Fcomponents%2Fredirect%0D%0Aproperty.2_value%3Dgirlscouts%2Fcomponents%2Fredirect%0D%0Aproperty.and%3Dtrue%0D%0Aproperty.operation%3Dunequals%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/etc/scaffolding/*/permanent-redirect/jcr:content/dialog/items/items/tab1/');
    map.put('path.exact', 'true');
    map.put('type', 'nt:unstructured');
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

editedNodes = [];
result.nodes.each { scaffolding ->
    nodePropertyMap.each { nodeName, propertyMap ->
        def node = scaffolding.getNodes(nodeName)[0];
        if (node == null) return;

        // Get node data and set new property
        def nodeObj = new TreeMap();
        nodeObj.put('path', node.path);
        def isGsusa = node.path.contains('gsusa');

        propertyMap.each { property, targetValue ->
            // Check for any path-specific assignments
            def newValue = targetValue;
            if (targetValue.contains(delimeter)) {
                def targetValueList = targetValue.split(delimeter);
                newValue = isGsusa ? targetValueList[0] : targetValueList[1];
            }

            // Do not change property if target value is already assigned
            def currentValue = node.get(property);
            if (currentValue == newValue) return;

            // Change property in node and save
            nodeObj.put('old_' + property, currentValue);
            node.set(property, newValue);
            nodeObj.put('new_' + property, node.get(property));
        }

        // Add to JSON if properties were changed
        if (nodeObj.size() > 1) editedNodes.add(nodeObj);
    }
    if (!filters.dryRun) {
        session.save();
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.nodes) {
    jsonMap.put('editedNodes', editedNodes);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numScaffoldingPagesQueried', result.nodes.size());
        dataMap.put('numNodesEdited', editedNodes.size());
    }
    if (buildJSON.filters) {
        dataMap.put('filters', filters);
    }
    jsonMap.put('data', dataMap);
}
json.call(jsonMap);
if (buildJSON.print) {
    println json.toPrettyString();
}
