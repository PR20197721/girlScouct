import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;


String[][] array = [["/content/girlscouts-template","/content/dam/girlscouts-girlscouts-template"],
                    ["/content/gsusa","/content/dam/girlscouts-gsusa"],
                    ["/content/gateway","/content/dam/gateway"],
                    ["/content/girlscoutcsa","/content/dam/southern-appalachian"],
                    ["/content/gsnetx","/content/dam/NE_Texas"],
                    ["/content/girlscoutsnccp","/content/dam/nc-coastal-pines-images-"],
                    ["/content/gswcf","/content/dam/wcf-images"],
                    ["/content/gssem","/content/dam/gssem"],
                    ["/content/gssjc","/content/dam/gssjc"],
                    ["/content/girlscouts-future","/content/dam/girlscouts-future"],
                    ["/content/gsctx","/content/dam/girlscouts-gsctx"],
                    ["/content/girlscoutsaz","/content/dam/girlscoutsaz"],
                    ["/content/girlscoutsnv","/content/dam/gssnv"],
                    ["/content/kansasgirlscouts","/content/dam/kansasgirlscouts"],
                    ["/content/gswestok","/content/dam/gswestok"],
                    ["/content/girlscouts-dxp2","/content/dam/girlscouts-girlscouts-dxp2"],
                    ["/content/gskentuckiana","/content/dam/gskentuckiana"],
                    ["/content/gswo","/content/dam/gswo"],
                    ["/content/gseok","/content/dam/girlscouts-gseok"],
                    ["/content/girlscoutsosw","/content/dam/oregon-sw-washington-"],
                    ["/content/gssn","/content/dam/gssn"],
                    ["/content/gsneo","/content/dam/gsneo"],
                    ["/content/girlscoutsofcolorado","/content/dam/girlscoutsofcolorado"],
                    ["/content/girlscoutstoday","/content/dam/girlscoutstoday"],
                    ["/content/gsbadgerland","/content/dam/gsbadgerland"],
                    ["/content/gscnc","/content/dam/girlscouts-gscnc"],
                    ["/content/girlscoutsoc","/content/dam/girlscoutsoc"],
                    ["/content/gscsnj","/content/dam/gscsnj"],
                    ["/content/gskentucky","/content/dam/girlscouts-gskentucky"],
                    ["/content/girlscoutsgcnwi","/content/dam/girlscouts-girlscoutsgcnwi"],
                    ["/content/sdgirlscouts","/content/dam/girlscouts-sdgirlscouts"],
                    ["/content/gsutah","/content/dam/girlscouts-gsutah"],
                    ["/content/gswpa","/content/dam/girlscouts-gswpa"],
                    ["/content/gsnorcal","/content/dam/girlscouts-gsnorcal"],
                    ["/content/gshpa","/content/dam/girlscouts-gshpa"],
                    ["/content/girlscoutsatl","/content/dam/girlscouts-girlscoutsatl"],
                    ["/content/gshnj","/content/dam/girlscouts-gshnj"],
                    ["/content/girlscoutsiowa","/content/dam/girlscouts-girlscoutsiowa"],
                    ["/content/gscwm","/content/dam/girlscouts-gscwm"],
                    ["/content/bdgsc","/content/dam/girlscouts-bdgsc"],
                    ["/content/girlscoutshh","/content/dam/girlscouts-girlscoutshh"],
                    ["/content/gshg","/content/dam/girlscouts-gshg"],
                    ["/content/girlscoutssoaz","/content/dam/girlscouts-girlscoutssoaz"],
                    ["/content/citrus-gs","/content/dam/girlscouts-citrus-gs"],
                    ["/content/gsgst","/content/dam/girlscouts-gsgst"],
                    ["/content/gsnypenn","/content/dam/girlscouts-gsnypenn"],
                    ["/content/girlscoutsww","/content/dam/girlscouts-girlscoutsww"],
                    ["/content/gs-top","/content/dam/girlscouts-gs-top"],
                    ["/content/girlscouts-swtx","/content/dam/girlscouts-girlscouts-swtx"],
                    ["/content/girlscoutshcc","/content/dam/girlscouts-girlscoutshcc"],
                    ["/content/girlscoutsesc","/content/dam/girlscouts-girlscoutsesc"],
                    ["/content/gsle","/content/dam/girlscouts-gsle"],
                    ["/content/girlscoutseasternmass","/content/dam/girlscouts-girlscoutseasternmass"],
                    ["/content/gsmw","/content/dam/girlscouts-gsmw"],
                    ["/content/jerseyshoregirlscouts","/content/dam/girlscouts-jerseyshoregirlscouts"],
                    ["/content/girlscoutsni","/content/dam/girlscouts-girlscoutsni"],
                    ["/content/gssc-mm","/content/dam/girlscouts-gssc-mm"],
                    ["/content/girlscoutsgwm","/content/dam/girlscouts-girlscoutsgwm"],
                    ["/content/girlscoutsofmaine","/content/dam/girlscouts-girlscoutsofmaine"],
                    ["/content/nmgirlscouts","/content/dam/girlscouts-nmgirlscouts"],
                    ["/content/girlscouts-gssi","/content/dam/girlscouts-girlscouts-gssi"],
                    ["/content/gsewni","/content/dam/girlscouts-gsewni"],
                    ["/content/gsoh","/content/dam/girlscouts-gsoh"],
                    ["/content/gswise","/content/dam/girlscouts-gswise"],
                    ["/content/gsnc","/content/dam/girlscouts-gsnc"],
                    ["/content/gssef","/content/dam/girlscouts-gssef"],
                    ["/content/gswny","/content/dam/girlscouts-gswny"],
                    ["/content/girlscouts-hawaii","/content/dam/girlscouts-girlscouts-hawaii"],
                    ["/content/girlscoutshs","/content/dam/girlscouts-girlscoutshs"],
                    ["/content/gssne","/content/dam/girlscouts-gssne"],
                    ["/content/girlscoutsnebraska","/content/dam/girlscouts-girlscoutsnebraska"],
                    ["/content/girlscoutsnorthernindiana-michiana","/content/dam/girlscouts-girlscoutsnorthernindiana-michiana"],
                    ["/content/gsvsc","/content/dam/girlscouts-gsvsc"],
                    ["/content/gsgcf","/content/dam/girlscouts-gsgcf"],
                    ["/content/girlscoutsindiana","/content/dam/girlscouts-girlscoutsindiana"],
                    ["/content/gscm","/content/dam/girlscouts-gscm"],
                    ["/content/gslpg","/content/dam/girlscouts-gslpg"],
                    ["/content/getyourgirlpower","/content/dam/girlscouts-getyourgirlpower"],
                    ["/content/girlscoutsccc","/content/dam/girlscouts-girlscoutsccc"],
                    ["/content/gsnnj","/content/dam/girlscouts-gsnnj"],
                    ["/content/juliettegordonlowbirthplace","/content/dam/girlscouts-juliettegordonlowbirthplace"],
                    ["/content/comgirlscouts","/content/dam/girlscouts-comgirlscouts"],
                    ["/content/gscfp","/content/dam/girlscouts-gscfp"],
                    ["/content/girlscoutsem","/content/dam/girlscouts-girlscoutsem"],
                    ["/content/girlscouts-ssc","/content/dam/girlscouts-girlscouts-ssc"],
                    ["/content/gsccc","/content/dam/girlscouts-gsccc"],
                    ["/content/gslakesandpines","/content/dam/girlscouts-gslakesandpines"],
                    ["/content/girlscoutsmoheartland","/content/dam/girlscouts-girlscoutsmoheartland"],
                    ["/content/gsnwgl","/content/dam/girlscouts-gsnwgl"],
                    ["/content/gsdakotahorizons","/content/dam/girlscouts-gsdakotahorizons"],
                    ["/content/girlscoutsp2p","/content/dam/girlscouts-girlscoutsp2p"],
                    ["/content/girlscoutsnca","/content/dam/girlscouts-girlscoutsnca"],
                    ["/content/girlscoutsfl","/content/dam/girlscouts-girlscoutsfl"],
                    ["/content/girlscoutsdiamonds","/content/dam/girlscouts-girlscoutsdiamonds"],
                    ["/content/gssgc","/content/dam/girlscouts-gssgc"],
                    ["/content/girlscoutssa","/content/dam/girlscouts-girlscoutssa"],
                    ["/content/girlscoutsccs","/content/dam/girlscouts-girlscoutsccs"],
                    ["/content/hngirlscouts","/content/dam/girlscouts-hngirlscouts"],
                    ["/content/gscb","/content/dam/girlscouts-gscb"],
                    ["/content/girlscoutsla","/content/dam/girlscouts-girlscoutsla"],
                    ["/content/gsep","/content/dam/girlscouts-gsep"],
                    ["/content/gsofsi","/content/dam/girlscouts-gsofsi"],
                    ["/content/gsofct","/content/dam/girlscouts-gsofct"],
                    ["/content/gsgms","/content/dam/girlscouts-gsgms"],
                    ["/content/gsdsw","/content/dam/girlscouts-gsdsw"],
                    ["/content/gsksmo","/content/dam/girlscouts-gsksmo"],
                    ["/content/gsmanitou","/content/dam/girlscouts-gsmanitou"],
                    ["/content/cgspr","/content/dam/girlscouts-cgspr"],
                    ["/content/girlscoutsalaska","/content/dam/girlscouts-girlscoutsalaska"],
                    ["/content/girlscoutsrv","/content/dam/girlscouts-girlscoutsrv"],
                    ["/content/usagson","/content/dam/girlscouts-usagson"],
                    ["/content/gssc","/content/dam/girlscouts-gssc"],
                    ["/content/girlscoutsnyc","/content/dam/girlscouts-girlscoutsnyc"],
                    ["/content/gsmists","/content/dam/girlscouts-gsmists"],
                    ["/content/gshom","/content/dam/girlscouts-gshom"]]

for (int i=0; i<array.length; i++) {
    Resource siteResource = resourceResolver.resolve(array[i][0]);
    if (siteResource != null) {
        Node sitePage = siteResource.adaptTo(Node.class);
        if (sitePage == null){
            println(siteResource.getPath());
        } else {
            Node jcrContent = sitePage.getNode("en").getNode("jcr:content");
            if (jcrContent.hasProperty("damPath")) {
                println("Will NOT add damPath to " + jcrContent.getPath());
            } else {
                jcrContent.setProperty("damPath", array[i][1]);
                println("Will add damPath to " + jcrContent.getPath());
            }
        }
    }
}
save();