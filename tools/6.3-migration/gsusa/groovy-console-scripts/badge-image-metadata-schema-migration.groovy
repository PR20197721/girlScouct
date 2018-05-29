import javax.jcr.Node
import org.apache.sling.api.resource.Resource
import java.util.*

List<String> BADGE_PATHS = Arrays.asList(
        "/content/dam/girlscouts-gsusa/images/Badges/Daisy/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Daisy/DaisyPetals/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Brownie/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Junior/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Cadettes/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Senior/jcr:content",
        "/content/dam/girlscouts-gsusa/images/Badges/Ambassador/jcr:content"
)

String SCHEMA_LOCATION = "/conf/global/settings/dam/adminui-extension/metadataschema/badge-image-schema"

for(String badgePath : BADGE_PATHS){
    Resource res = resourceResolver.resolve(badgePath)
    Node node = res.adaptTo(Node.class)
    node.setProperty("metadataSchema", SCHEMA_LOCATION)
    save()
}
