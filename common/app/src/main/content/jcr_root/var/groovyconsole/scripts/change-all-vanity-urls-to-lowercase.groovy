import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
property = 'sling:vanityPath';

// Filter
filters = [
    dryRun: true,
    publish: false,
    allCouncils: true, // Set to false to only search councils in the list below
    councils: [
        'girlscoutseasternmass'
    ]
];

// Results
buildJSON = [
    print: true,
    vanityPaths: true,
    errors: true,
    hitCount: true,
    filters: true
];

//*
//*
//* QUERY

def buildQuery(data) {
    Map map = [:];

    if (data.allCouncils) {
        map.put('path', '/content');
    } else {
        map.put('group.p.or', 'true');
        data.councils.eachWithIndex { council, i ->
            map.put('group.' + i + '_path', '/content/' + council);
        }
    }
    map.put('type', 'nt:unstructured');
    map.put('property', property);
    map.put('property.operation', 'exists');
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

extensionPattern = Pattern.compile('\\.[a-z]{1,5}$'); // Matches any file ".ext" 1-5 chars, case-insensitive
def containsIgnoredExtension(path) {
    Matcher m = extensionPattern.matcher(path);
    return m.find();
}

externalSitePattern = Pattern.compile('\\w+\\.\\w+\\/'); // Matches "sitename.ext/"
def isExternalRedirect(path) {
    Matcher m = externalSitePattern.matcher(path);
    return m.find();
}

def isValidVanityPath(path) {
    def isLowerCase = path == path.toLowerCase();
    return isLowerCase || containsIgnoredExtension(path) || isExternalRedirect(path);
}

errors = new TreeMap();
def activatePath(path) {
    try {
        activate(path);
    } catch (Exception ex) {
        errors.put(path, ex.message);
    }
    sleep(1000);
}

pathMap = new TreeMap();
editedNodes = [];
uneditedNodes = [];

result.nodes.each { node ->
    // Get node data and set new property
    def nodeObj = new TreeMap();
    def vanityPathProp = node.get(property);
    nodeObj.put('title', node.get('jcr:title'));
    nodeObj.put('path', node.path);

    // Vanity path can be stored as a string or a list, in either case convert to list
    def vanityPathList = [];
    if (vanityPathProp instanceof String) {
        vanityPathList.add(vanityPathProp);
    } else {
        vanityPathList.addAll(vanityPathProp);
    }

    vanityPathSet = [] as SortedSet;
    vanityPathList.each { vanityPath ->
        // Check for valid vanity path
        if (isValidVanityPath(vanityPath)) {
            // Add to map
            pathMap[vanityPath] = 'VALID';
            vanityPathSet.add(vanityPath);
        } else {
            // Fix vanity path
            def newVanityPath = vanityPath.toLowerCase();

            // Add mapping
            pathMap[vanityPath] = newVanityPath;
            pathMap[newVanityPath] = 'VALID';

            // Duplicates handled by set
            vanityPathSet.add(newVanityPath);
        }
    }

    def setList = vanityPathSet as List;
    if (vanityPathList.sort() == setList) {
        // No changes
        nodeObj.put(property, vanityPathProp);
        uneditedNodes.add(nodeObj);
    } else {
        // Change property in node and save
        nodeObj.put('old_' + property, vanityPathProp);
        if (vanityPathProp instanceof String) {
            node.set(property, setList[0]);
        } else {
            node.set(property, setList);
        }
        nodeObj.put('new_' + property, node.get(property));
        editedNodes.add(nodeObj);
        if (!filters.dryRun) {
            session.save();
            if (filters.publish) {
                activatePath(node.path);
            }
        }
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.vanityPaths) {
    jsonMap.put('editedNodes', editedNodes);
    jsonMap.put('uneditedNodes', uneditedNodes);
    jsonMap.put('pathMap', pathMap);
}
if (buildJSON.errors && !errors.empty) {
    jsonMap.put('errors', errors);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numNodesQueried', result.nodes.size());
        dataMap.put('numValid', pathMap.findAll{it.value == 'VALID'}.size());
        dataMap.put('numInvalid', pathMap.findAll{it.value != 'VALID'}.size());
        dataMap.put('numVanityPaths', pathMap.size());
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
