
package org.girlscouts.vtk.models;

import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;

public class Kai implements AtomicTypeConverter{

	public Value getValue(ValueFactory valueFactory, Object object) {
		// TODO Auto-generated method stub
		System.err.println("tester2");	
		return null;
	}

	public Object getObject(Value value) {
		// TODO Auto-generated method stub
System.err.println("tester1");		
		return null;
	}

	public String getXPathQueryValue(ValueFactory valueFactory, Object object) {
		// TODO Auto-generated method stub
		System.err.println("tester3");	
		return null;
	}
	
}
