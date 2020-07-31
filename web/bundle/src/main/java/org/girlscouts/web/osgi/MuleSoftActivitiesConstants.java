package org.girlscouts.web.osgi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface MuleSoftActivitiesConstants {
    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    String CONFIG_PATH = "/etc/data-import/girlscouts/salesforce-events";
}
