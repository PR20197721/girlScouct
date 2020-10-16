/* groovylint-disable DuplicateMapLiteral */
import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES

// Filter
filters = [
    dryRun: true,
    councils: [
        old: '322',
        'new': '313'
    ],
    finances: [
        folder: '/finances/',
        reports: '/finances/reports'
    ],
    years: [
        '2019',
        '2018',
        '2017',
        '2016',
        '2015'
    ]
];

// Results
buildJSON = [
    print: true,
    nodes: false,
    troops: true,
    hitCount: true,
    filters: true
];

//*
//*
//* QUERY

// http://34.203.143.115:4503/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=1_group.p.or%3Dtrue%0D%0A1_group.1_path%3D%2Fvtk2019%2F322%2Ftroops%2F*%2Ffinances%2F%0D%0A1_group.1_path.exact%3Dtrue%0D%0A1_group.2_path%3D%2Fvtk2018%2F322%2Ftroops%2F*%2Ffinances%2F%0D%0A1_group.2_path.exact%3Dtrue%0D%0A1_group.3_path%3D%2Fvtk2019%2F313%2Ftroops%2F*%2Ffinances%2F%0D%0A1_group.3_path.exact%3Dtrue%0D%0A1_group.4_path%3D%2Fvtk2018%2F313%2Ftroops%2F*%2Ffinances%2F%0D%0A1_group.4_path.exact%3Dtrue%0D%0A1_group.5_path%3D%2Fvtk2019%2F322%2Ftroops%2F*%2Ffinances%2Freports%0D%0A1_group.5_path.exact%3Dtrue%0D%0A1_group.6_path%3D%2Fvtk2018%2F322%2Ftroops%2F*%2Ffinances%2Freports%0D%0A1_group.6_path.exact%3Dtrue%0D%0A1_group.7_path%3D%2Fvtk2019%2F313%2Ftroops%2F*%2Ffinances%2Freports%0D%0A1_group.7_path.exact%3Dtrue%0D%0A1_group.8_path%3D%2Fvtk2018%2F313%2Ftroops%2F*%2Ffinances%2Freports%0D%0A1_group.8_path.exact%3Dtrue%0D%0Atype%3Dnt%3Aunstructured%0D%0A2_group.p.or%3Dtrue%0D%0A2_group.1_property%3DsubpathDir%0D%0A2_group.1_property.operation%3Dexists%0D%0A2_group.2_property%3Dattached_file_emailed%0D%0A2_group.2_property.operation%3Dexists%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dselective%0D%0Ap.properties%3Djcr%3Apath+sling%3AinternalRedirect%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('1_group.p.or', 'true');
    def i = 0;
    data.councils.each { c, council ->
        data.years.each { year ->
            data.finances.each { t, type ->
                map.put('1_group.' + i + '_path', '/vtk' + year + '/' + council + '/troops/*' + type);
                map.put('1_group.' + i + '_path.exact', 'true');
                i++;
            }
        }
    }

    map.put('type', 'nt:unstructured');
    /*map.put('2_group.p.or', 'true');
    map.put('2_group.1_property', 'subpathDir'); // folder
    map.put('2_group.1_property.exists', 'exists');
    map.put('2_group.2_property', 'attached_file_emailed'); // reports
    map.put('2_group.2_property.exists', 'exists');*/
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

nodes = new TreeMap();
result.nodes.each { node ->
    def troopPath = node.path.split(filters.finances.folder)[0];
    def financePath = node.path.split(filters.finances.folder)[1];
    def council = troopPath.contains('/' + filters.councils.new + '/') ? filters.councils.new : filters.councils.old;

    if (!nodes.containsKey(council)) {
        nodes.put(council, new TreeMap());
    }
    if (!nodes[council].containsKey(troopPath)) {
        def troopNode = node.parent.parent;
        def troopNodeMap = new TreeMap();
        troopNodeMap.put('name', troopNode.get('sfTroopName'));
        troopNodeMap.put('nodes', [] as SortedSet);
        nodes[council].put(troopPath, troopNodeMap);
    }
    nodes[council][troopPath].nodes.add(financePath);
}

def copyNode(oldTroopPath, oldTroopMap, newTroopPath, newTroopMap) {
    if (resourceResolver.getResource(newTroopPath) == null) {
        // troop
        oldTroopMap.put('copyTo', newTroopPath);
        if (!filters.dryRun) copy(oldTroopPath, newTroopPath);
    } else if (resourceResolver.getResource(newTroopPath + filters.finances.folder) == null) {
        // troop/finances
        oldTroopMap.put('copyTo', newTroopPath + filters.finances.folder);
        if (!filters.dryRun) copy(oldTroopPath + filters.finances.folder, newTroopPath + filters.finances.folder);
    } else {
        // troop/finances/children
        oldTroopMap.put('copyTo', [] as SortedSet);
        oldTroopMap.nodes.each { node ->
            if (!newTroopMap.nodes.contains(node)) {
                oldTroopMap.copyTo.add(newTroopPath + filters.finances.folder + node);
                if (!filters.dryRun) copy(oldTroopPath + filters.finances.folder + node, newTroopPath + filters.finances.folder + node);
            }
        }
    }

    /*oldTroopMap.copyTo.each { node ->

    }*/
}

oldNodes = nodes[filters.councils.old];
newNodes = nodes[filters.councils.new];
troopsFullyMapped = new TreeMap();
troopsDifferentTroopId = new TreeMap();
troopsDifferentTroopIdMultiple = new TreeMap();
troopsDifferentTroopName = new TreeMap();
troopsOrphaned = new TreeMap();
oldNodes.each { oldTroopPath, oldTroopMap ->
    def newTroopPath = oldTroopPath.replaceFirst(filters.councils.old, filters.councils.new);
    def newTroopMap = newNodes[newTroopPath];
    if (newTroopMap != null) {
        if (newTroopMap.name == oldTroopMap.name) {
            // 1.) If fully mapped, just copy over
            copyNode(oldTroopPath, oldTroopMap, newTroopPath, newTroopMap);
            troopsFullyMapped.put(oldTroopPath, oldTroopMap);
        } else {
            oldTroopMap.put('newTroopName', newTroopMap.name);

            // 2.) If different troop name, copy over to the existing troop node under a different id that has it, otherwise create a new troop node
            copyNode(oldTroopPath, oldTroopMap, newTroopPath, newTroopMap);
            troopsDifferentTroopName.put(oldTroopPath, oldTroopMap);
        }
    } else {
        // Filter by old troop name
        def newTroopRenamed = newNodes.findAll { it.value.name == oldTroopMap.name };
        def newTroopIds = newTroopRenamed.keySet() as List; // Need list for unique - https://stackoverflow.com/a/53446999

        // Filter for unique troop id - http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/Collection.html#unique(groovy.lang.Closure)
        def uniqueNewTroopIds = newTroopIds.unique(false) { a, b -> a.split('/').last() <=> b.split('/').last() };

        // Filter by old troop id if possible
        newTroopIds = uniqueNewTroopIds.findAll { it.split('/').last() == oldTroopPath.split('/').last() } ?: uniqueNewTroopIds;

        def oldVtkYear = oldTroopPath.split('/')[1];
        if (newTroopIds.size() == 1) {
            newTroopIds.each { newTroopId ->
                newTroopPath = newTroopId.replaceFirst('vtk\\d{4}', oldVtkYear);
                newTroopMap = newNodes[newTroopPath] ?: [nodes:[]];
                oldTroopMap.put('newTroopId', newTroopId);

                // 3.) If different troop id, copy over to the existing troop node under the same name that has it, otherwise create a new troop node
                copyNode(oldTroopPath, oldTroopMap, newTroopPath, newTroopMap);
                if (uniqueNewTroopIds.size() > 1) {
                    oldTroopMap.put('possibleTroopIds', newTroopRenamed.keySet());
                    troopsDifferentTroopIdMultiple.put(oldTroopPath, oldTroopMap);
                } else {
                    troopsDifferentTroopId.put(oldTroopPath, oldTroopMap);
                }
            }
        } else {
            // 4.) Do nothing if nodes cannot be mapped in any way
            //copyNode(oldTroopPath, oldTroopMap, newTroopPath, [nodes:[]]);
            troopsOrphaned.put(oldTroopPath, oldTroopMap);
        }
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.nodes) {
    jsonMap.put('oldNodes', oldNodes);
    jsonMap.put('newNodes', newNodes);
}
if (buildJSON.troops) {
    jsonMap.put('troopsFullyMapped', troopsFullyMapped);
    jsonMap.put('troopsDifferentTroopId', troopsDifferentTroopId);
    jsonMap.put('troopsDifferentTroopIdMultiple', troopsDifferentTroopIdMultiple);
    jsonMap.put('troopsDifferentTroopName', troopsDifferentTroopName);
    jsonMap.put('troopsOrphaned', troopsOrphaned);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numNodesQueried', result.nodes.size());
        dataMap.put('numOldCouncilTroops', oldNodes.size());
        dataMap.put('numNewCouncilTroops', newNodes.size());
        dataMap.put('numTroopsFullyMapped', troopsFullyMapped.size());
        dataMap.put('numTroopsDifferentTroopId', troopsDifferentTroopId.size());
        dataMap.put('numTroopsDifferentTroopIdMultiple', troopsDifferentTroopIdMultiple.size());
        dataMap.put('numTroopsDifferentTroopName', troopsDifferentTroopName.size());
        dataMap.put('numTroopsOrphaned', troopsOrphaned.size());
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
