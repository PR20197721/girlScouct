package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JcrCalendarPropertyConverter implements AtomicTypeConverter {

    private static Logger log = LoggerFactory.getLogger(JcrCalendarPropertyConverter.class);
    private final SimpleDateFormat jcrTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssZ");

    @Override
    public Value getValue(ValueFactory valueFactory, Object propValue) {
        if (propValue == null){
            return null;
        }
        return valueFactory.createValue((Calendar) propValue);
    }

    @Override
    public Object getObject(Value value) {
        if (value == null){
            return null;
        }
        Calendar cal = null;
        try{
            cal = value.getDate();
        }catch (Exception e){
            log.error("Error occurred:", e);
        }
        return cal;
    }

    @Override
    public String getXPathQueryValue(ValueFactory valueFactory, Object object) {
        return jcrTimeFormat.format((Calendar) object);
    }
}
