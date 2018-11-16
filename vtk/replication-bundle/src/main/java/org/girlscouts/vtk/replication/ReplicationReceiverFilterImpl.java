package org.girlscouts.vtk.replication;

import java.util.Dictionary;
import java.util.regex.Pattern;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts VTK Replication Receiver Filter", description = "Filter out paths that should not be received.", metatype = true, immediate = true)
@Service(ReplicationReceiverFilter.class)
@Properties({
    @Property(
        name = "allowedRegexes",
        cardinality = Integer.MAX_VALUE,
        label = "Allowed Regexes",
        description = "Nodes under (including) these paths are accepted. Others are rejected."
    )
})
public class ReplicationReceiverFilterImpl implements ReplicationReceiverFilter {
	private String[] allowedRegexes;
	private Pattern[] allowedPatterns;
	private static Logger log = LoggerFactory.getLogger(ReplicationReceiverFilterImpl.class);
	
	@Modified
	@Activate
	private void updateConfig(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		allowedRegexes = (String[])configs.get("allowedRegexes");
		allowedPatterns = new Pattern[allowedRegexes.length];
		for (int i = 0; i < allowedRegexes.length; i++) {
			String allowedRegex = allowedRegexes[i];
			allowedPatterns[i] = Pattern.compile(allowedRegex);
			log.info("Allowed regex: " + allowedRegex);
		}
	}

	public boolean accept(String path) {
		for (Pattern allowedPattern : allowedPatterns) {
			if (allowedPattern.matcher(path).matches()) {
				log.debug("Accept path: " + path);
				return true;
			}
		}
		log.debug("Reject path: " + path);
		return false;
	}
}
