<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.commons.beanutils.BeanMap, org.apache.commons.beanutils.PropertyUtilsBean,org.apache.commons.beanutils.BeanComparator, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%

	//Contact one= new Contact();
	//Contact two= new Contact();
	
	Test one = new Test();
	one.setId("test");
	Test two= new Test();
	two.setId("test");
	
	//org.apache.commons.beanutils.BeanComparator<Contact> comparator = new org.apache.commons.beanutils.BeanComparator<Contact>("id");
    //org.apache.commons.beanutils.BeanComparator comparator = new org.apache.commons.beanutils.BeanComparator();
    //out.println("** "+comparator.compare(one, two));
    
  try{  compareObjects(one, two); }catch(Exception e){e.printStackTrace();}
%>


<%!

public void compareObjects(Object oldObject, Object newObject)  {
   try{
	System.err.println(1);
	//BeanMap map = new BeanMap(oldObject);

    PropertyUtilsBean propUtils = new PropertyUtilsBean();

    //for (Object propNameObject : map.keySet()) {
        String propertyName = "id";// (String) propNameObject;
        System.err.println("** "+ propertyName);
        Object property1 = propUtils.getProperty(oldObject, propertyName);
        Object property2 = propUtils.getProperty(newObject, propertyName);
        if (property1.equals(property2)) {
            System.err.println("  " + propertyName + " is equal");
        } else {
            System.err.println("> " + propertyName + " is different (oldValue=\"" + property1 + "\", newValue=\"" + property2 + "\")");
        }
    //}
   }catch(Exception e){e.printStackTrace();}

}
%>