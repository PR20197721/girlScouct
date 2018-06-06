import javax.jcr.Node
import org.apache.sling.api.resource.Resource
import java.util.*

String BASE_PATH = "/content/usergenerated";

List<String> MISSING_FOLDERS = Arrays.asList(
        "gsoh",
        "girlscoutshcc",
        "rollout61-2",
        "girlscouts-swtx",
        "gateway",
        "girlscoutsindiana",
        "comgirlscouts",
        "girlscoutsnebraska",
        "gssgc",
        "gsmists",
        "girlscoutsalaska",
        "juliettegordonlowbirthplace",
        "gsgst",
        "rollout-61",
        "hngirlscouts",
        "getyourgirlpower",
        "gsccc",
        "gssne",
        "girlscoutsnyc",
        "gscsnj",
        "bdgsc",
        "webtocase",
        "gsbulkeditor-test",
        "gswpa",
        "gssc",
        "cgspr",
        "gsdakotahorizons",
        "gssn",
        "girlscoutsesc",
        "gssem",
        "gsnc",
        "girlscoutssoaz",
        "gsney",
        "gslakesandpines",
        "gscwm",
        "girlscoutsp2p",
        "gs-top",
        "girlscoutsnorthernindiana-michiana",
        "gswestok",
        "seo-test-council",
        "girlscouts-prototype",
        "test3",
        "gsnorcal",
        "girlscoutsdiamonds",
        "gstest",
        "gswcf",
        "girlscouts-template",
        "gssc-mm",
        "gsewni",
        "gsmanitou",
        "gskentucky",
        "girlscouts-dxp2",
        "gsnnj",
        "gswise",
        "gsksmo",
        "gscfp",
        "seo-permission-test-2",
        "gsofsi",
        "gshpa",
        "girlscoutsgcnwi",
        "girlscoutsnca",
        "gswny",
        "girlscoutsosw",
        "gscb",
        "girlscouts-dxp",
        "girlscoutsla",
        "girlscoutsofcolorado",
        "girlscoutsnccp",
        "girlscoutsrv",
        "gssef",
        "gsgms",
        "jerseyshoregirlscouts",
        "nmgirlscouts",
        "girlscoutsaz",
        "seo-test-042516",
        "girlscoutsnv",
        "girlscoutsfl",
        "girlscoutsofmaine",
        "gshom"
)

Resource base = resourceResolver.resolve(BASE_PATH)
Node baseNode = base.adaptTo(Node.class);

String[] mixins = new String[2]
mixins[1] = "rep:AccessControllable"

for(String missingFolderPath : MISSING_FOLDERS){
    Resource potentialResource = resourceResolver.resolve(BASE_PATH + "/" + missingFolderPath)
    if(potentialResource != null && !"sling:nonexisting".equals(potentialResource.getResourceType())){
        continue
    }
    Node newNode = baseNode.addNode(missingFolderPath, "sling:Folder")
    newNode.setProperty("sling:mixinTypes", mixins)
    save()
}