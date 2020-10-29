package org.girlscouts.common.osgi.component.impl;

import org.girlscouts.common.osgi.component.GirlscoutsVtkConfigProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Component(service = {
		GirlscoutsVtkConfigProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsVtkConfigProviderImpl")
@Designate(ocd = GirlscoutsVtkConfigProviderImpl.Config.class)
public class GirlscoutsVtkConfigProviderImpl implements GirlscoutsVtkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsVtkConfigProviderImpl.class);

	private String helloUrl=null;
	private String loginUrl=null;
	private String logoutUrl=null;
    private String renewUrl=null;
	private String[] councilMapping=null;


	@Activate
	private void activate(Config config) {
		this.helloUrl=config.helloUrl();
		this.loginUrl=config.loginUrl();
		this.logoutUrl=config.logoutUrl();
		this.councilMapping=config.councilMapping();
		this.renewUrl = config.renewUrl();
        log.info("Girl Scouts VTK Config Provider Activated.");
	}

    @Override
    public String getHelloUrl() {
        return helloUrl;
    }
    @Override
    public String getLoginUrl() {
        return loginUrl;
    }
    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }
    @Override
    public String[] getCouncilMapping() {
        return councilMapping;
    }
    @Override
    public String getRenewUrl() {
        return renewUrl;
    }

    @Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts VTK Config Provider Deactivated.");
	}

    @ObjectClassDefinition(name = "Girl Scouts VTK settings provider Configuration", description = "Girl Scouts VTK settings provider Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Hello Url", description = "Hello Url", type = AttributeType.STRING)
        String helloUrl() default "/content/girlscouts-vtk/controllers/hello.hello.js";

        @AttributeDefinition(name = "VTK Log In URL", type = AttributeType.STRING)
        String loginUrl() default "/content/girlscouts-vtk/sso/landing.html";;

        @AttributeDefinition(name = "VTK Log Out URL", type = AttributeType.STRING)
        String logoutUrl() default "/system/sling/logout?resource=/content/girlscouts-vtk";

        @AttributeDefinition(name = "Member Community URL", type = AttributeType.STRING)
        String renewUrl() default "#";

        @AttributeDefinition(name = "Council Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway")
        String[] councilMapping() default {
                "SUM::vtkcontent",
                "IRM::vtkcontent",
                "999::vtkcontent",
                "313::gateway",
                "597::gsnetx",
                "388::girlscoutcsa",
                "367::girlscoutsnccp",
                "465::gssem",
                "320::gswcf",
                "234::gsneo",
                "661::gssn",
                "664::girlscoutsosw",
                "240::gswo",
                "465::gssem",
                "607::girlscoutsaz",
                "536::kansasgirlscouts",
                "563::gswestok",
                "564::gseok",
                "512::girlscoutsofcolorado",
                "591::gssjc",
                "131::gscsnj",
                "636::gsnorcal",
                "212::gskentucky",
                "623::sdgirlscouts",
                "263::gswpa",
                "467::gsbadgerland",
                "116::gscwm",
                "622::girlscoutsoc",
                "660::girlscoutsnv",
                "514::girlscoutstoday",
                "524::girlscoutsiowa",
                "430::girlscoutsgcnwi",
                "688::girlscoutsww",
                "674::gsutah",
                "208::gskentuckiana",
                "312::citrus-gs",
                "289::bdgsc",
                "608::girlscoutssoaz",
                "204::gscnc",
                "169::gsnypenn",
                "155::girlscoutshh",
                "333::girlscoutsatl",
                "557::nmgirlscouts",
                "583::gs-top",
                "126::girlscoutsgwm",
                "117::girlscoutseasternmass",
                "654::gsmw",
                "578::gsctx",
                "415::girlscoutsni",
                "134::jerseyshoregirlscouts",
                "238::gsoh",
                "001::girlscouts-future",
                "321::gssef",
                "387::girlscoutshs",
                "192::gswny",
                "438::girlscoutsnorthernindiana-michiana",
                "687::gsewni",
                "634::girlscoutshcc",
                "110::girlscoutsofmaine",
                "441::girlscouts-gssi",
                "376::girlscoutsesc",
                "497::gswise",
                "635::girlscoutsccc",
                "345::gslpg",
                "218::gscm",
                "282::gsccc",
                "402::getyourgirlpower",
                "258::gshpa",
                "140::gsnnj",
                "647::girlscouts-ssc",
                "434::girlscoutsindiana",
                "346::gsle",
                "642::girlscouts-hawaii",
                "590::girlscouts-swtx",
                "468::gslakesandpines",
                "314::gsgcf",
                "168::gsnc",
                "612::girlscoutsccs",
                "194::gssne",
                "499::gsnwgl",
                "278::comgirlscouts",
                "506::girlscoutsdiamonds",
                "901::gsusa",
                "135::gshnj",
                "368::girlscoutsp2p",
                "306::girlscoutsnca",
                "360::hngirlscouts",
                "200::gscb",
                "253::gsep",
                "307::girlscoutssa",
                "478::gsdakotahorizons",
                "614::gssgc",
                "556::girlscoutsnebraska",
                "548::girlscoutsem",
                "322::gscfp",
                "354::gsgms",
                "596::gsgst",
                "538::girlscoutsmoheartland",
                "582::gsdsw",
                "547::gsksmo",
                "281::gsvsc",
                "611::girlscoutsla",
                "377::gssc-mm",
                "493::gsmanitou",
                "198::cgspr",
                "603::girlscoutsalaska",
                "106::gsofct",
                "153::girlscoutsneny",
                "325::gshg",
                "450::gsmists",
                "191::gssc",
                "319::girlscoutsfl",
                "161::girlscoutsnyc",
                "700::usagson",
                "416::gsofsi"
        };
    }

    @Override
    public String toString() {
        return "GirlscoutsVtkConfigProviderImpl{" + "helloUrl='" + helloUrl + '\'' + ", loginUrl='" + loginUrl + '\'' + ", logoutUrl='" + logoutUrl + '\'' + ", renewUrl='" + renewUrl + '\'' +  ", councilMapping=" + Arrays.toString(councilMapping) + '}';
    }
}