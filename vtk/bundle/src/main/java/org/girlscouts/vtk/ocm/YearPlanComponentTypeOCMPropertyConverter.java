package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class YearPlanComponentTypeOCMPropertyConverter implements AtomicTypeConverter {

    private static Logger log = LoggerFactory.getLogger(YearPlanComponentTypeOCMPropertyConverter.class);

    @Override
    public Value getValue(ValueFactory valueFactory, Object propValue) {
        if (propValue == null){
            return null;
        }
        return valueFactory.createValue(String.valueOf(propValue));
    }

    @Override
    public Object getObject(Value value) {
        if (value == null){
            return null;
        }
        try {
            switch (value.getString()) {
                case "MEETING":
                    return YearPlanComponentType.MEETING;
                case "ACTIVITY":
                    return YearPlanComponentType.ACTIVITY;
                case "MEETINGCANCELED":
                    return YearPlanComponentType.MEETINGCANCELED;
                case "MILESTONE":
                    return YearPlanComponentType.MILESTONE;
            }
        }catch(Exception e){

            }
        return YearPlanComponentType.MEETING;
    }

    @Override
    public String getXPathQueryValue(ValueFactory valueFactory, Object object) {
        return String.valueOf(object);
    }
}
