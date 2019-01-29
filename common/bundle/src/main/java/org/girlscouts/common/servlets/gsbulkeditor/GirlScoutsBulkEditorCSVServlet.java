package org.girlscouts.common.servlets.gsbulkeditor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.value.ValueHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.scripting.core.ScriptHelper;
import org.girlscouts.common.encryption.FormEncryption;
import org.girlscouts.common.events.search.GSDateTime;
import org.girlscouts.common.events.search.GSDateTimeFormat;
import org.girlscouts.common.events.search.GSDateTimeFormatter;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.text.Text;
import com.day.text.XMLChar;

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Girl Scouts Bulk Editor CSV Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/services/gsbulkeditor/export/csv" })
public class GirlScoutsBulkEditorCSVServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GirlScoutsBulkEditorCSVServlet.class);

	@Activate
	private void activate(ComponentContext context) {
		log.info("Girlscouts GS Bulkeditor CSV Servlet Activated.");
	}

	/**
	 * Query clause
	 */
	public static final String QUERY_PARAM = "query";
	public static final String DEEP_SEARCH_PARAM = "isDeep";
	public static final String RESOURCE_TYPE_PARAM = "resourceType";
	public static final String PRIMARY_TYPE_PARAM = "primaryType";
	public static final String IMPORT_TYPE_PARAM = "importType";
    public static final String YEAR_PARAM = "year";

	public static final String SEPARATOR_PARAM = "separator";

	final static char NAME_VALUE_SEPARATOR = 30;
	final static char VALUE_SEPARATOR = 31;
	final static char PARA_SEPARATOR = 29;

	/**
	 * Common path prefix
	 */
	public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

	/**
	 * Common path prefix
	 */
	public static final String PROPERTIES_PARAM = "cols";

	public static final String DEFAULT_SEPARATOR = ",";

	public static final String VALUE_DELIMITER = "\"";

	public static final String CHARACTER_ENCODING = "windows-1252";

	/**
	 * Encoding param
	 */
	public static final String ENCODING_PARAM = "charset";

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		log.debug("Girlscouts GS Bulkeditor CSV Servlet processing.");
		StringWriter buf = new StringWriter();
		BufferedWriter bw = new BufferedWriter(buf);

		String queryString = request.getParameter(QUERY_PARAM);

		String isDeepString = request.getParameter(DEEP_SEARCH_PARAM);
		String resourceTypeString = request.getParameter(RESOURCE_TYPE_PARAM);
		String primaryTypeString = request.getParameter(PRIMARY_TYPE_PARAM);
		String importType = request.getParameter(IMPORT_TYPE_PARAM);

		Boolean isDeep = "true".equals(isDeepString);

		ResourceResolver rr = request.getResourceResolver();
		Session session = rr.adaptTo(Session.class);
		try {

			String tmp = request.getParameter(PROPERTIES_PARAM);
			String[] properties = (tmp != null) ? tmp.split(",") : null;

			final String separator = DEFAULT_SEPARATOR;

			Boolean includePath = true;
			if (importType != null) {
				if (!importType.equals("contacts") && !importType.equals("documents") && !importType.equals("events")) {
					bw.write(valueParser(JcrConstants.JCR_PATH, separator) + separator);
				} else {
					includePath = false;
				}
			} else {
				bw.write(valueParser(JcrConstants.JCR_PATH, separator) + separator);
			}
			if (properties != null) {
				for (String property : properties) {
					property = property.trim();
					if (importType != null) {
						if (importType.equals("documents")) {
							property = property.replaceAll("jcr:content/metadata/dc:title", "Title")
									.replaceAll("jcr:content/metadata/dc:description", "Description")
									.replaceAll("jcr:content/metadata/cq:tags", "Categories");
						} else if (importType.equals("events")) {
							switch (property) {
							case "jcr:content/jcr:title":
								property = "Title";
								break;
							case "jcr:content/data/start-date":
								property = "Start Date";
								break;
							case "jcr:content/data/start-time":
								property = "Start Time";
								break;
							case "jcr:content/data/end-date":
								property = "End Date";
								break;
							case "jcr:content/data/end-time":
								property = "End Time";
								break;
							case "jcr:content/data/timezone":
								property = "Time Zone (Only enter if you want timezone to be visible e.g. 10:30 PM EST. See http://joda-time.sourceforge.net/timezones.html for valid IDs)";
								break;
							case "jcr:content/data/region":
								property = "Region";
								break;
							case "jcr:content/data/locationLabel":
								property = "Location Name";
								break;
							case "jcr:content/data/address":
								property = "Address";
								break;
							case "jcr:content/data/details":
								property = "Text";
								break;
							case "jcr:content/data/srchdisp":
								property = "Search Description";
								break;
							case "jcr:content/data/color":
								property = "Color";
								break;
							case "jcr:content/data/register":
								property = "Registration";
								break;
							case "jcr:content/cq:tags-categories":
								property = "Categories";
								break;
							case "jcr:content/cq:tags-progLevel":
								property = "Program Levels";
								break;
							case "jcr:content/data/image/fileReference":
								property = "Image";
								break;
							case "jcr:content/data/regOpen-date":
								property = "Registration Open Date";
								break;
							case "jcr:content/data/regOpen-time":
								property = "Registration Open Time";
								break;
							case "jcr:content/data/regClose-date":
								property = "Registration Close Date";
								break;
							case "jcr:content/data/regClose-time":
								property = "Registration Close Time";
								break;
							case "jcr:content/data/progType":
								property = "Program Type";
								break;
							case "jcr:content/data/grades":
								property = "Grades";
								break;
							case "jcr:content/data/girlFee":
								property = "Girl Fee";
								break;
							case "jcr:content/data/adultFee":
								property = "Adult Fee";
								break;
							case "jcr:content/data/minAttend":
								property = "Minimum Attendance";
								break;
							case "jcr:content/data/maxAttend":
								property = "Maximum Attendance";
								break;
							case "jcr:content/data/programCode":
								property = "Program Code";
								break;
							}
						}
					}
					bw.write(valueParser(property, separator) + separator);
				}
			}

			if (importType != null) {
				if (importType.equals("documents") || importType.equals("events")) {
					bw.write(valueParser("Path", separator) + separator);
				}
			}

			bw.newLine();

			String path = queryString.split(":")[1];

			String year = request.getParameter(YEAR_PARAM);
			if (null != importType) {
				if (importType.equals("events")) {
					if (null != year) {
						path = path + "/" + year;
					}
				}
			}

			if (null != rr.getResource(path)) {
				iterateNodes(path, separator, bw, properties, session, request, isDeep, resourceTypeString,
						primaryTypeString, includePath, importType, rr);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}

		// send string buffer
		bw.flush();
		response.setContentType("text/csv");
		String encoding = request.getParameter(ENCODING_PARAM);
		response.setCharacterEncoding(StringUtils.isNotBlank(encoding) ? encoding : CHARACTER_ENCODING);
		response.getWriter().print(buf.getBuffer().toString());
	}

	public static String valueParser(String value, String separator) {
		if (value != null) {
			if (value.indexOf(separator) != -1 || value.indexOf('\n') != -1) {
				value = VALUE_DELIMITER + value + VALUE_DELIMITER;
			}
		}
		return value;
	}

	/**
	 * Formats the given jcr property to the enhanced docview syntax.
	 *
	 * @param prop
	 *            the jcr property
	 * @return the formatted string
	 * @throws RepositoryException
	 *             if a repository error occurs
	 */
	public static String format(Property prop, ResourceResolver rr, String additional) throws RepositoryException {
		StringBuffer attrValue = new StringBuffer();
		int type = prop.getType();
		if ((type == PropertyType.BINARY || isAmbiguous(prop)) && additional == null) {
			attrValue.append("{");
			attrValue.append(PropertyType.nameFromValue(prop.getType()));
			attrValue.append("}");
		}
		// only write values for non binaries
		if (prop.getType() != PropertyType.BINARY) {
			if (additional != null && prop.getName().endsWith("cq:tags")) {
				if (prop.getDefinition().isMultiple()) {
					Value[] values = prop.getValues();
					for (int i = 0; i < values.length; i++) {
						TagManager tm = rr.adaptTo(TagManager.class);
						Tag t = tm.resolve(values[i].getString());
						String strValue = ValueHelper.serialize(values[i], false);
						if (t != null) {
							if (i > 0 && attrValue.length() > 0) {
								if (null != additional) {
									if (t.getTagID().matches("^.*:" + additional + "/.*$")) {
										attrValue.append(';');
									}
								}
							}
							strValue = t.getTitle();
							if (strValue.contains("\n") || strValue.contains("\r")) {
								strValue = strValue.replaceAll("(\\r|\\n)", "");
							}
							if (null != additional) {
								if (t.getTagID().matches("^.*:" + additional + "/.*$")) {
									attrValue.append(strValue);
								}
							} else {
								attrValue.append(strValue);
							}
						}
					}
				} else {
					String strValue = ValueHelper.serialize(prop.getValue(), false);
					TagManager tm = rr.adaptTo(TagManager.class);
					Tag t = tm.resolve(prop.getValue().getString());
					if (t != null) {
						strValue = t.getTitle();
					}
					if (strValue.contains("\n") || strValue.contains("\r")) {
						strValue = strValue.replaceAll("(\\r|\\n)", "");
					}
					if (null != additional) {
						if (t.getTagID().matches("^.*:" + additional + "/.*$")) {
							attrValue.append(strValue);
						}
					}
				}
			} else if (additional != null && (prop.getName().equals("start") || prop.getName().equals("end")
					|| prop.getName().equals("regOpen") || prop.getName().equals("regClose"))) {
				String datetimeString = prop.getString();
				GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
				GSDateTime dt = GSDateTime.parse(datetimeString, dtfIn);
				GSDateTimeFormatter dtfOutDate = GSDateTimeFormat.forPattern("MM/dd/yyyy");
				GSDateTimeFormatter dtfOutTime = GSDateTimeFormat.forPattern("hh:mm a");
				String dateTime = "";
				if (additional.equals("date")) {
					dateTime = dtfOutDate.print(dt);
					attrValue.append(dateTime);
				} else if (additional.equals("time")) {
					dateTime = dtfOutTime.print(dt);
					attrValue.append(dateTime);
				}
			} else if (prop.getName().equals("timezone")) {
				Pattern p = Pattern.compile("^\\(.*\\).*\\((.*)\\)$");
				Matcher m = p.matcher(prop.getString());
				if (m.matches()) {
					attrValue.append(m.group(1));
				} else {
					attrValue.append(prop.getString());
				}
			}

			else if (prop.getDefinition().isMultiple()) {
				attrValue.append('[');
				Value[] values = prop.getValues();
				for (int i = 0; i < values.length; i++) {
					if (i > 0) {
						attrValue.append(',');
					}
					String strValue = ValueHelper.serialize(values[i], false);
					switch (prop.getType()) {
					case PropertyType.STRING:
					case PropertyType.NAME:
					case PropertyType.PATH:
						escape(attrValue, strValue, true);
						break;
					default:
						attrValue.append(strValue);
					}
				}
				attrValue.append(']');
			} else {
				String strValue = ValueHelper.serialize(prop.getValue(), false);
				escape(attrValue, strValue, false);
			}
			String strValue = attrValue.toString();
			if (strValue.contains(",")) {
				strValue = strValue.replace("\"", "\"\"");
				strValue = "\"" + strValue + "\"";
			}
			if (strValue.isEmpty()) {
				strValue = "\"\"";
			}
			if (strValue.contains("\n") || strValue.contains("\r")) {
				strValue = strValue.replaceAll("(\\r|\\n)", "");
			}
			return strValue;
		}
		return attrValue.toString();
	}

	public static String format(String[] values) throws RepositoryException {
		StringBuffer attrValue = new StringBuffer();
		if (values.length > 1) {
			attrValue.append('[');
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					attrValue.append(',');
				}
				attrValue.append(values[i]);
			}
			attrValue.append(']');
		} else {
			String strValue = values.length == 0 ? "" : values[0];
			escape(attrValue, strValue, false);
		}
		String strValue = attrValue.toString();
		if (strValue.contains(",")) {
			strValue = strValue.replace("\"", "\"\"");
			strValue = "\"" + strValue + "\"";
		}
		if (strValue.isEmpty()) {
			strValue = "\"\"";
		}
		if (strValue.contains("\n") || strValue.contains("\r")) {
			strValue = strValue.replaceAll("(\\r|\\n)", "");
		}
		return strValue;

	}

	/**
	 * Escapes the value
	 *
	 * @param buf
	 *            buffer to append to
	 * @param value
	 *            value to escape
	 * @param isMulti
	 *            indicates multi value property
	 */
	protected static void escape(StringBuffer buf, String value, boolean isMulti) {
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\\') {
				buf.append("\\\\");
			} else if (c == ',' && isMulti) {
				buf.append("\\,");
			} else if (i == 0 && !isMulti && (c == '[' || c == '{')) {
				buf.append('\\').append(c);
			} else if (XMLChar.isInvalid(c)) {
				buf.append("\\u");
				buf.append(Text.hexTable[(c >> 12) & 15]);
				buf.append(Text.hexTable[(c >> 8) & 15]);
				buf.append(Text.hexTable[(c >> 4) & 15]);
				buf.append(Text.hexTable[c & 15]);
			} else {
				buf.append(c);
			}
		}
	}

	/**
	 * Checks if the type of the given property is ambiguous in respect to it's
	 * property definition. the current implementation just checks some well
	 * known properties.
	 *
	 * @param prop
	 *            the property
	 * @return type
	 * @throws RepositoryException
	 *             if a repository error occurs
	 */
	public static boolean isAmbiguous(Property prop) throws RepositoryException {
		return prop.getType() != PropertyType.STRING && !UNAMBIGOUS.contains(prop.getName());
	}

	/**
	 * set of unambigous property names
	 */
	private static final Set<String> UNAMBIGOUS = new HashSet<String>();

	static {
		UNAMBIGOUS.add("jcr:primaryType");
		UNAMBIGOUS.add("jcr:mixinTypes");
		UNAMBIGOUS.add("jcr:lastModified");
		UNAMBIGOUS.add("jcr:created");
	}

	public void iterateNodes(String path, String separator, BufferedWriter bw, String[] properties, Session session,
			SlingHttpServletRequest request, Boolean isDeep, String resourceType, String primaryType,
			Boolean includePath, String importType, ResourceResolver rr) throws Exception {
		NodeIterator iter = session.getNode(path).getNodes();
		// while (hits.hasNext()) {
		while (iter.hasNext()) {
			Node node = iter.nextNode();
			// Row hit = hits.nextRow();
			// Node node = (Node)
			// session.getItem(hit.getValue(JcrConstants.JCR_PATH).getString());
			if (node != null && (!isDeep || (isDeep && !node.getPath().endsWith("jcr:content")))) {
				Boolean canIterate = false;
				if (null != resourceType && null != primaryType) {
					if (node.hasProperty("sling:resourceType") && node.hasProperty("jcr:primaryType")) {
						if (!node.getProperty("sling:resourceType").getString().equals(resourceType)
								&& !node.getProperty("jcr:primaryType").getString().equals(primaryType)) {
							canIterate = true;
						}
					} else if (node.hasProperty("jcr:content/sling:resourceType")
							&& node.hasProperty("jcr:content/jcr:primaryType")) {
						if (!node.getProperty("jcr:content/sling:resourceType").getString().equals(resourceType)
								&& !node.getProperty("jcr:content/jcr:primaryType").getString().equals(primaryType)) {
							canIterate = true;
						}
					} else {
						canIterate = true;
					}
				} else if (null != resourceType) {
					if (node.hasProperty("sling:resourceType")) {
						if (!node.getProperty("sling:resourceType").getString().equals(resourceType)) {
							canIterate = true;
						}
					} else if (node.hasProperty("jcr:content/sling:resourceType")) {
						if (!node.getProperty("jcr:content/sling:resourceType").getString().equals(resourceType)) {
							canIterate = true;
						}
					} else {
						canIterate = true;
					}
				} else if (null != primaryType) {
					if (node.hasProperty("jcr:primaryType")) {
						if (!node.getProperty("jcr:primaryType").getString().equals(primaryType)) {
							canIterate = true;
						}
					} else if (node.hasProperty("jcr:content/jcr:primaryType")) {
						if (!node.getProperty("jcr:content/jcr:primaryType").getString().equals(primaryType)) {
							canIterate = true;
						}
					} else {
						canIterate = true;
					}
				}
				if (canIterate) {
					if (node.hasNodes() && isDeep) {
						iterateNodes(node.getPath(), separator, bw, properties, session, request, isDeep, resourceType,
								primaryType, includePath, importType, rr);
					}
					continue;
				}

				if (null != importType) {
					if (importType.equals("documents")) {
						if (!node.hasProperty("jcr:content/metadata/dc:format")) {
							continue;
						} else {
							String format = node.getProperty("jcr:content/metadata/dc:format").getString();
							if (!format.equals("application/pdf")
									&& !format
											.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
									&& !format.equals("application/zip")
									&& !format.equals("application/vnd.ms-powerpoint")
									&& !format.equals("application/vnd.ms-excel")
									&& !format
											.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
									&& !format
											.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
									&& !format.equals("application/msword")
									&& !format.equals("application/vnd.ms-word.document.macroenabled.12")
									&& !format.equals("text/csv")
									&& !format.equals("application/vnd.ms-excel.sheet.macroenabled.12")
									&& !format.equals("text/plain")) {
								continue;
							}
						}
					}
				}

				if (null != importType) {
					if (!importType.equals("documents") && !importType.equals("events")) {
						if (includePath) {
							bw.write(valueParser(node.getPath(), separator) + separator);
						}
					} else {
						if (includePath) {
							bw.write(valueParser(node.getPath(), separator) + separator);
						}
					}
				} else if (includePath) {
					bw.write(valueParser(node.getPath(), separator) + separator);
				}
				if (node.hasProperty("isEncrypted") && node.getProperty("isEncrypted").getString().equals("true")) {
					Map<String, String[]> decryptedMap = getNodeSecret(node, request);
					if (properties != null) {
						for (String property : properties) {
							property = property.trim();
							if (decryptedMap.containsKey(property)) {
								String[] values = decryptedMap.get(property);
								bw.write(format(values));
							} else if (node.hasProperty(property)) {
								Property prop = node.getProperty(property);
								bw.write(format(prop, rr, null));
							}
							bw.write(separator);
						}
					}
				} else {
					if (properties != null) {
						for (String property : properties) {
							property = property.trim();
							if (property.equals("jcr:content/cq:tags-progLevel")
									|| property.equals("jcr:content/cq:tags-categories")) {
								if (node.hasProperty(property.substring(0, property.lastIndexOf("-")))) {
									Property prop = node.getProperty(property.substring(0, property.lastIndexOf("-")));
									String tagFolder = property.substring(property.lastIndexOf("-") + 1,
											property.length());
									if (tagFolder.equals("progLevel")) {
										tagFolder = "program-level";
									}
									bw.write(format(prop, rr, tagFolder));
								}
							} else if (property.equals("jcr:content/data/start-date")
									|| property.equals("jcr:content/data/start-time")
									|| property.equals("jcr:content/data/end-date")
									|| property.equals("jcr:content/data/end-time")
									|| property.equals("jcr:content/data/regOpen-date")
									|| property.equals("jcr:content/data/regOpen-time")
									|| property.equals("jcr:content/data/regClose-date")
									|| property.equals("jcr:content/data/regClose-time")) {
								if (node.hasProperty(property.substring(0, property.lastIndexOf("-")))) {
									Property prop = node.getProperty(property.substring(0, property.lastIndexOf("-")));
									String dateType = property.substring(property.lastIndexOf("-") + 1,
											property.length());
									bw.write(format(prop, rr, dateType));
								}
							}
							else if (node.hasProperty(property)) {
								Property prop = node.getProperty(property);
								bw.write(format(prop, rr, null));
							}
							bw.write(separator);
						}
					}
				}

				if (null != importType) {
					if (importType.equals("documents") || importType.equals("events")) {
						bw.write(valueParser(node.getPath(), separator) + separator);
					}
				}
				bw.newLine();
				if (node.hasNodes() && isDeep) {
					iterateNodes(node.getPath(), separator, bw, properties, session, request, isDeep, resourceType,
							primaryType, includePath, importType, rr);
				}
			}
		}
	}

	public static Map<String, String[]> getNodeSecret(Node node, SlingHttpServletRequest request)
			throws ItemNotFoundException {
		try {
			String secret = node.getProperty("secret").getString();

			SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
			ScriptHelper scriptHelper = (ScriptHelper) bindings.getSling();
			FormEncryption fEn = scriptHelper.getService(FormEncryption.class);

			String decrypted = fEn.decrypt(secret);
			String[] propStrings = decrypted.split(String.valueOf(PARA_SEPARATOR));
			Map<String, String[]> propsMap = new HashMap<String, String[]>();
			for (String propString : propStrings) {
				String[] tmpStrings = propString.split(String.valueOf(NAME_VALUE_SEPARATOR));
				String propName = tmpStrings[0];
				String[] values = tmpStrings[1].split(String.valueOf(VALUE_SEPARATOR));
				propsMap.put(propName, values);
			}
			return propsMap;

		} catch (Exception e) {
			throw new ItemNotFoundException("The node is encrypted, but no secret found");
		}

	}
}