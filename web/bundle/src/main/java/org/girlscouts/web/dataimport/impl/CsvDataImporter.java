package org.girlscouts.web.dataimport.impl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.girlscouts.web.dataimport.DataImporter;
import org.girlscouts.web.exception.GirlScoutsException;

import com.day.text.csv.Csv;

public class CsvDataImporter implements DataImporter {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SX";
    private Reader reader;
    private Session session;
    private String confPath;
    private Map<String, String> fieldTypeMap;
    private String keyField = null;
    private int keyFieldIndex = 0;
    private String getNameScript = null;
    private String destPath;
    private String[] colKeys;
    private String[] colTypes;

    public CsvDataImporter(Reader reader, Session session, String confPath,
	    String destPath) throws GirlScoutsException {
	this.reader = reader;
	this.session = session;
	this.confPath = confPath;
	this.destPath = destPath;

	initConf();
    }

    public void importIntoJcr() throws GirlScoutsException {
	Iterator<String[]> lineIter;
	try {
	    lineIter = new Csv().read(reader);
	} catch (IOException e) {
	    throw new GirlScoutsException(e,
		    "IO Exception while importing CSV.");
	}

	int lineCount = 0;
	while (lineIter.hasNext()) {
	    String[] cols = lineIter.next();
	    if (lineCount == 0) {
		// First line: These are column names
		colKeys = new String[cols.length];
		colTypes = new String[cols.length];
		// Read every column and save their types into an array
		for (int i = 0; i < cols.length; i++) {
		    colKeys[i] = cols[i];
		    colTypes[i] = fieldTypeMap.get(cols[i]);
		    // Save the key field index
		    if (fieldTypeMap.get(cols[i]).equals(keyField)) {
			keyFieldIndex = i;
		    }
		}
	    } else {
		// Following lines: importing data
		Node node;
		String nodePath = destPath + getNodeName(cols[keyFieldIndex]);
		try {
		    node = session.getRootNode().addNode(nodePath);
		} catch (ItemExistsException e) {
		    throw new GirlScoutsException(e, "Itme already exists: "
			    + nodePath);
		} catch (PathNotFoundException e) {
		    throw new GirlScoutsException(e, "Path not found: "
			    + nodePath);
		} catch (VersionException e) {
		    throw new GirlScoutsException(e, "Version Exception");
		} catch (ConstraintViolationException e) {
		    throw new GirlScoutsException(e,
			    "Contraint Violation while adding node: "
				    + nodePath);
		} catch (LockException e) {
		    throw new GirlScoutsException(e, "Lock Exception");
		} catch (RepositoryException e) {
		    throw new GirlScoutsException(e, "Repository Exception");
		}
		for (int i = 0; i < cols.length; i++) {
		    try {
			setProperty(node, colKeys[i], cols[i], colTypes[i]);
		    } catch (GirlScoutsException e) {
			
		    }
		}
	    }
	    lineCount++;
	}
    }

    private void initConf() throws GirlScoutsException {
	try {
	    Node confNode = session.getNode(confPath);
	    if (confNode.hasProperty("keyField")) {
		keyField = confNode.getProperty("keyField").getString();
	    }
	    if (confNode.hasProperty("getNameScript")) {
		getNameScript = confNode.getProperty("getNameScript")
			.getString();
	    }

	    // Initializing the field type map
	    fieldTypeMap = new HashMap<String, String>();
	    Node fieldsNode = confNode.getNode("fields");
	    PropertyIterator iter = fieldsNode.getProperties();
	    while (iter.hasNext()) {
		Property property = iter.nextProperty();
		fieldTypeMap.put(property.getName(), property.getString());
	    }
	} catch (PathNotFoundException e) {
	    throw new GirlScoutsException(e, "Path not found: confPath");
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Repository Exception");
	}
    }
    
    private String getNodeName(String keyValue) throws GirlScoutsException {
	if (getNameScript == null) {
	    return keyValue;
	} else {
	    ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine engine = manager.getEngineByName("JavaScript");

	    // evaluate script
	    String script = "getName = " + getNameScript;
	    try {
		engine.eval(script);
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("getName", keyValue);
	    } catch (ScriptException e) {
		throw new GirlScoutsException(e, "Error executing JavaScript: "
			+ script);
	    } catch (NoSuchMethodException e) {
		throw new GirlScoutsException(e, "No JavaScript engine found.");
	    }
	}
    }

    private void setProperty(Node node, String key, String value, String type)
	    throws GirlScoutsException {
	try {
	    if (type.equals("string")) {
		node.setProperty(key, value);
	    } else if (type.equals("boolean")) {
		node.setProperty(key, value.equalsIgnoreCase("true"));
	    } else if (type.indexOf("date") == 0) {
		String formatStr = type.substring("date".length() + 1,
			type.length() - 1);
		if (formatStr.isEmpty())
		    formatStr = CsvDataImporter.DEFAULT_DATE_FORMAT;
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = sdf.parse(value);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		node.setProperty(key, cal);
	    }
	} catch (ValueFormatException e) {
	    throw new GirlScoutsException(e,
		    "Value Format exception while saving property: " + key
			    + ":" + value);
	} catch (VersionException e) {
	    throw new GirlScoutsException(e, "Version Exception");
	} catch (LockException e) {
	    throw new GirlScoutsException(e, "Lock Exception");
	} catch (ConstraintViolationException e) {
	    throw new GirlScoutsException(e,
		    "Contraint Violation while saving property: " + key + ":"
			    + value);
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Repository Exception");
	} catch (ParseException e) {
	    throw new GirlScoutsException(e, "Error parsing Date value: "
		    + value);
	}
    }
}