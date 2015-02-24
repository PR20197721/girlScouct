package org.girlscouts.vtk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GSUtils {
	public static String getDocTypeImageFromString(String str) {
		String extension = getDocExtensionFromString(str);
		String docTypeImage = null;
		if (extension.equals("pdf")) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-pdf.png";
		} else if (extension.indexOf("ind") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-indesign.png";
		} else if (extension.indexOf("htm") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-html.png";
		} else if (extension.indexOf("xls") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-excel.png";
		} else if (extension.equals("ai")) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-illustrator.png";
		} else if (extension.indexOf("ppt") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-powerpoint.png";
		} else if (extension.indexOf("doc") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-word.png";
		} else if (extension.equals("psd")) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-photoshop.png";
		} else if (extension.equals("txt")) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-text.png";
		}
		return docTypeImage;
	}
	public static String getDocExtensionFromString(String str) {
		String regexStr = "\\.([a-z]*)$";
		Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		String extension = "";
		if (matcher.find()) {
			extension = matcher.group(1).toLowerCase();
		} else {
			extension = str;
		}
		return extension;
	}
}
