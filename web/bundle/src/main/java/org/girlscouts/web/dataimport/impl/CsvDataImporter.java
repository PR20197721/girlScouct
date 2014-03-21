package org.girlscouts.web.dataimport.impl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.dataimport.DataImporter;
import org.girlscouts.web.exception.GirlScoutsException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.text.csv.Csv;

public class CsvDataImporter implements DataImporter {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SX";

    private static Logger log = LoggerFactory.getLogger(CsvDataImporter.class);

    private Reader reader;
    private ResourceResolver rr;
    private String confPath;
    private String destPath;

    private List<String[]> fields;
    private List<String[]> defaultFields;
    private String primaryType;
    private boolean isNameFromField;
    private String nameFromField;
    private String nameScript;
    private String[] nameScriptFields;
    private int[] nameFieldIndexes;
    private String dryRunPath;

    public CsvDataImporter(Reader reader, ResourceResolver rr, String confPath,
	    String destPath) throws GirlScoutsException {
	this.reader = reader;
	this.rr = rr;
	this.confPath = confPath;
	this.destPath = destPath;
	this.dryRunPath = null;

	initConf();
    }

    private void initConf() throws GirlScoutsException {
	try {
	    Node confNode = rr.resolve(confPath).adaptTo(Node.class);
	    if (confNode == null) {
		throw new GirlScoutsException(null, "Conf path not found: "
			+ confPath);
	    }

	    // Read primary type
	    if (confNode.hasProperty("primaryType")) {
		primaryType = confNode.getProperty("primaryType").getString();
	    } else {
		primaryType = "nt:unstructured";
	    }

	    // Read name gen conf
	    Node nameGenNode = confNode.getNode("nameGen");
	    if (nameGenNode.hasProperty("fromField")) {
		isNameFromField = true;
		nameFromField = nameGenNode.getProperty("fromField")
			.getString();
	    } else {
		isNameFromField = false;
		nameScript = nameGenNode.getProperty("script").getString();
		nameScriptFields = nameGenNode.getProperty("scriptFields")
			.getString().split(",");
	    }

	    // Prepare nameFieldIndexes
	    if (isNameFromField) {
		nameFieldIndexes = new int[1];
	    } else {
		nameFieldIndexes = new int[nameScriptFields.length];
	    }
	    // Read fields
	    fields = new ArrayList<String[]>();
	    Node fieldsNode = confNode.getNode("fields");
	    NodeIterator nodeIter = fieldsNode.getNodes();
	    while (nodeIter.hasNext()) {
		Node node = nodeIter.nextNode();
		String key = node.getName();
		String type = node.hasProperty("type") ? node.getProperty(
			"type").getString() : "string";
		String script = node.hasProperty("script") ? node.getProperty(
			"script").getString() : null;

		String[] confArr = new String[3];
		confArr[0] = key;
		confArr[1] = type;
		confArr[2] = script;
		fields.add(confArr);

		// Remember the indexes of script fields
		if (!isNameFromField) {
		    for (int i = 0; i < nameScriptFields.length; i++) {
			if (nameScriptFields[i].equals(key)) {
			    nameFieldIndexes[i] = fields.size() - 1;
			}
		    }
		} else {
		    if (nameFromField.equals(key)) {
			nameFieldIndexes[0] = fields.size() - 1;
		    }
		}
	    }

	    // Read default fields
	    defaultFields = new ArrayList<String[]>();
	    Node defaultFieldsNode = confNode.getNode("defaultFields");
	    if (defaultFieldsNode != null) {
		nodeIter = defaultFieldsNode.getNodes();
		while (nodeIter.hasNext()) {
		    Node node = nodeIter.nextNode();
		    String key = node.getName();
		    String type = node.hasProperty("type") ? node.getProperty(
			    "type").getString() : "string";
		    String value = node.getProperty("value").getString();
		    String[] confArr = new String[3];
		    confArr[0] = key;
		    confArr[1] = type;
		    confArr[2] = value;
		    defaultFields.add(confArr);
		}
	    }
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e,
		    "Repository Exception while reading import configuration: "
			    + confPath + ". Reason: " + e.getMessage());
	}
    }

    public String getDryRunPath() throws GirlScoutsException {
	if (this.dryRunPath == null) {
	    throw new GirlScoutsException(null, "Dry run never executed.");
	}
	return this.dryRunPath;
    }

    public String[] doImport() throws GirlScoutsException {
	List<String> errors = new ArrayList<String>();

	Node tmpParentNode = rr.resolve(dryRunPath).adaptTo(Node.class);
	Node destParentNode = rr.resolve(destPath).adaptTo(Node.class);
	NodeIterator iter;
	try {
	    iter = tmpParentNode.getNodes();
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Cannot get child nodes.");
	}
	while (iter.hasNext()) {
	    Node node = iter.nextNode();
	    try {
		JcrUtil.copy(node, destParentNode, node.getName());
	    } catch (RepositoryException e) {
		try {
		    errors.add("Error saving node: " + node.getName());
		} catch (RepositoryException e1) {
		    errors.add("Error saving unkown node");
		}
	    }
	}
	try {
	    tmpParentNode.remove();
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Cannot remove the temp folder");
	}
	return errors.toArray(new String[errors.size()]);
    }

    public String[] doDryRun() throws GirlScoutsException {
	List<String> errors = new ArrayList<String>();
	Iterator<String[]> lineIter;
	try {
	    lineIter = new Csv().read(reader);
	} catch (IOException e) {
	    throw new GirlScoutsException(e,
		    "IO Exception while importing CSV.");
	}

	// Generate tmp folder
	String tmpName = Long.toString(System.currentTimeMillis()) + "-" + Integer.toString(new Random().nextInt(1000));
	this.dryRunPath = DataImporter.TMP_ROOT + "/" + tmpName;
	try {
	    Node tmpRootNode = rr.resolve(DataImporter.TMP_ROOT).adaptTo(Node.class);
            tmpRootNode.addNode(tmpName, "nt:unstructured");
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Cannot create tmp folder: "
		    + dryRunPath);
	}

	int lineCount = 0;
	while (lineIter.hasNext()) {
	    lineCount++;
	    String[] cols = lineIter.next();
	    try {
		List<Object> result = readLine(cols);
		String nodeName = getName(result);

		String actualPath = destPath + "/" + nodeName;
		if (!rr.resolve(actualPath).getResourceType().equals("sling:nonexisting")) {
		    throw new GirlScoutsException(null, "Node already exists: "
			    + actualPath);
		}

		Node parentNode = rr.resolve(dryRunPath).adaptTo(Node.class);
		saveNode(parentNode, nodeName, result);
	    } catch (GirlScoutsException e) {
		errors.add("Error on line: " + lineCount + ": " + e.getReason());
	    }
	}
	try {
	    rr.adaptTo(Session.class).save();
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Repository Exception while saving temp nodes.");
	}
	return errors.toArray(new String[errors.size()]);
    }

    private void saveNode(Node parentNode, String nodeName, List<Object> values)
	    throws GirlScoutsException {
	try {
	    Node node = parentNode.addNode(nodeName, primaryType);
	    if (primaryType.equals("cq:Page")) {
		node = node.addNode("jcr:content", "cq:PageContent");
	    }

	    for (String[] field : defaultFields) {
		saveProperty(node, field[0], field[2], field[1]);
	    }

	    int i = 0;
	    for (String[] field : fields) {
		saveProperty(node, field[0], values.get(i), field[1]);
		i++;
	    }
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Error while saving node: "
		    + nodeName);
	}
    }

    private String getName(List<Object> values) throws GirlScoutsException {
	if (isNameFromField) {
	    return (String)values.get(nameFieldIndexes[0]);
	} else {
	    String[] scriptParams = new String[nameFieldIndexes.length];
	    for (int i = 0; i < scriptParams.length; i++) {
		scriptParams[i] = (String)values.get(nameFieldIndexes[i]);
	    }
	    return executeJavaScript(nameScript, scriptParams);
	}
    }

    private String executeJavaScript(String nameScript, String... value)
	    throws GirlScoutsException {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < value.length - 1; i++) {
	    sb.append("'").append(value[i]).append("',");
	}
	if (value.length > 0) {
	    sb.append("'").append(value[value.length - 1]).append("'");
	}
	String args = sb.toString();

	Context cx = Context.enter();
	Scriptable scope = cx.initStandardObjects();
	String script = "var getName = " + nameScript + "; getName(" + args + ");";
	Object result = cx.evaluateString(scope, script, "script", 1, null);
	return (String)result;
    }

    private List<Object> readLine(String[] cols) throws GirlScoutsException {
	List<Object> result = new ArrayList<Object>();
	for (int i = 0; i < cols.length; i++) {
	    String type = fields.get(i)[1];
	    String script = fields.get(i)[2];
	    String value = cols[i];

	    // Execute script
	    if (script != null) {
		value = executeJavaScript(script, value);
	    }

	    if (type.equals("string")) {
		result.add(value);
	    } else if (type.equals("boolean")) {
		result.add(new Boolean(value.equalsIgnoreCase("true") ? true
			: false));
	    } else if (type.startsWith("date")) {
		String formatStr = type.substring("date".length() + 1,
			type.length() - 1);
		if (formatStr.isEmpty()) {
		    formatStr = CsvDataImporter.DEFAULT_DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date;
		try {
		    date = sdf.parse(value);
		} catch (ParseException e) {
		    throw new GirlScoutsException(e, "Error parsing date \""
			    + value + "\" using format \"" + formatStr + "\"");
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		result.add(cal);
	    }
	}
	return result;
    }

    private void saveProperty(Node node, String key, Object value, String type)
	    throws GirlScoutsException {
	try {
	    if (type.equals("string")) {
		node.setProperty(key, (String) value);
	    } else if (type.equals("boolean")) {
		node.setProperty(key, ((Boolean) value).booleanValue());
	    } else if (type.indexOf("date") == 0) {
		node.setProperty(key, (Calendar) value);
	    }
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e,
		    "Repository Exception while setting property: " + key);
	}
    }
}