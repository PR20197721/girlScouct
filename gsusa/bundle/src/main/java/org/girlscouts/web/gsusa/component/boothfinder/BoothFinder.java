package org.girlscouts.web.gsusa.component.boothfinder;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts Cookie Booth Finder", description = "Find nearby cookie booth", metatype = true, immediate = true)
@Service
@Properties({
		@Property(name = "apiBasePath", label = "API Base Path", description = "The base path of Girl Scouts cookie booth API")
})
public class BoothFinder {
	private static final long serialVersionUID = -1155198001819957909L;
	private static Logger log = LoggerFactory.getLogger(BoothFinder.class);
}
