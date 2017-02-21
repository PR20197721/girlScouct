/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package apps.wcm.core.components.gsbulkeditor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.ServletException;

import org.apache.jackrabbit.commons.query.GQL;
import org.apache.jackrabbit.value.ValueHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.commons.lang.StringUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.text.Text;
import com.day.text.XMLChar;

/**
 * Servers as base for image servlets
 */
public class tsv extends SlingAllMethodsServlet {
    /**
     * Query clause
     */
    public static final String QUERY_PARAM = "query";

    public static final String SEPARATOR_PARAM = "separator";

    /**
     * Common path prefix
     */
    public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

    /**
     * Common path prefix
     */
    public static final String PROPERTIES_PARAM = "cols";

    public static final String DEFAULT_SEPARATOR = "\t";

    public static final String VALUE_DELIMITER = "\"";

    public static final String CHARACTER_ENCODING = "windows-1252";

    /**
     * Encoding param
     */
    public static final String ENCODING_PARAM = "charset";

    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws ServletException, IOException {

        StringWriter buf = new StringWriter();
        BufferedWriter bw = new BufferedWriter(buf);

        String queryString = request.getParameter(QUERY_PARAM);
        String commonPathPrefix = request.getParameter(COMMON_PATH_PREFIX_PARAM);

        Session session = request.getResourceResolver().adaptTo(
                Session.class);
        try {
            RowIterator hits;
            if (commonPathPrefix != null && queryString != null) {
                hits = GQL.execute(queryString, session, commonPathPrefix);
            } else if (queryString != null) {
                hits = GQL.execute(queryString, session);
            } else {
                return;
            }

            String tmp = request.getParameter(PROPERTIES_PARAM);
            String[] properties = (tmp != null) ? tmp.split(",") : null;

            //final String separator = (request.getParameter(SEPARATOR_PARAM)!=null ? request.getParameter(SEPARATOR_PARAM) : DEFAULT_SEPARATOR);
            final String separator = DEFAULT_SEPARATOR;

            bw.write(tsv.valueParser(JcrConstants.JCR_PATH, separator));
            if (properties != null) {
                for (String property : properties) {
                    property = property.trim();
                    bw.write(separator + tsv.valueParser(property, separator));
                }
            }

            bw.newLine();

            while (hits.hasNext()) {
                Row hit = hits.nextRow();
                Node node = (Node) session.getItem(hit.getValue(JcrConstants.JCR_PATH).getString());
                if (node != null) {
                    bw.write(tsv.valueParser(hit.getValue(JcrConstants.JCR_PATH).getString(), separator));
                    if (properties != null) {
                        for (String property : properties) {
                            bw.write(separator);
                            property = property.trim();
                            if (node.hasProperty(property)) {
                                Property prop = node.getProperty(property);
                                bw.write(tsv.format(prop));
                            }
                        }
                    }
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // send string buffer
        bw.flush();
        response.setContentType("text/tsv");
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
     * @param prop the jcr property
     * @return the formatted string
     * @throws RepositoryException if a repository error occurs
     */
    public static String format(Property prop) throws RepositoryException {
        StringBuffer attrValue = new StringBuffer();
        int type = prop.getType();
        if (type == PropertyType.BINARY || isAmbiguous(prop)) {
            attrValue.append("{");
            attrValue.append(PropertyType.nameFromValue(prop.getType()));
            attrValue.append("}");
        }
        // only write values for non binaries
        if (prop.getType() != PropertyType.BINARY) {
            if (prop.getDefinition().isMultiple()) {
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
        }
        return attrValue.toString();
    }

    /**
     * Escapes the value
     *
     * @param buf     buffer to append to
     * @param value   value to escape
     * @param isMulti indicates multi value property
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
     * @param prop the property
     * @return type
     * @throws RepositoryException if a repository error occurs
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


}