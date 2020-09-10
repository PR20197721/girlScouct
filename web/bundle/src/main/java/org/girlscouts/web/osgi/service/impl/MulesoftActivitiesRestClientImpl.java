package org.girlscouts.web.osgi.service.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.girlscouts.common.osgi.service.impl.BasicGirlScoutsService;
import org.girlscouts.web.osgi.MuleSoftActivitiesConstants;
import org.girlscouts.web.osgi.service.MulesoftActivitiesRestClient;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component(service = {MulesoftActivitiesRestClient.class}, immediate = true, name = "org.girlscouts.web.osgi.service.impl.MulesoftActivitiesRestClientImpl")
@Designate(ocd = MulesoftActivitiesRestClientImpl.Config.class)
public class MulesoftActivitiesRestClientImpl extends BasicGirlScoutsService implements MulesoftActivitiesRestClient, MuleSoftActivitiesConstants {
    private static Logger log = LoggerFactory.getLogger(MulesoftActivitiesRestClientImpl.class);
    private String restEndPoint;
    private String client_id;
    private String client_secret;

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.restEndPoint = getConfig("restEndPoint");
        this.client_id = getConfig("clientId");
        this.client_secret = getConfig("clientSecret");
        log.info("MuleSoft Activities Rest Client Activated.");
    }

    @Override
    public String getEvents(Date asOfDate) {
        DateFormat ACTIVITY_DATE_FORMAT = new SimpleDateFormat(MODIFIED_DATE_FORMAT);
        String modifiedTime = ACTIVITY_DATE_FORMAT.format(asOfDate);
        String url = this.restEndPoint+"?modifiedTime="+modifiedTime;
        try {
            String json = doGet(url);
            log.debug(json);
            return json;
        } catch (Exception e) {
            log.error("Error occurred getting activities  from mulesoft ", e);
        }
        return null;
    }



    private String doGet(String url) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("client_id", this.client_id);
            getRequest.addHeader("client_secret", this.client_secret);
            log.debug("curl -X GET '" + url + "' -H 'Accept: application/json' -H 'client_id: " + this.client_id + "'  -H 'client_secret: " + this.client_secret + "'");
            HttpResponse response = httpClient.execute(getRequest);
            String json = getJsonFromResponse(response);
            return json;
        } catch (Exception e) {
            log.error("Exception is thrown making a GET call to " + url, e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing http client to " + url, e);
            }
        }
        return null;
    }

    private String getJsonFromResponse(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Mulesoft API status code: " + response.getStatusLine().getStatusCode() + " : " + response);
            throw new RuntimeException("Mulesoft API : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        entity.getContent();
        String json = EntityUtils.toString(entity);
        log.debug("Mulesoft Response: " + response);
        EntityUtils.consume(entity);
        return json;
    }

    @ObjectClassDefinition(name = "MuleSoft Activities Rest client configuration", description = "MuleSoft Activities Rest client configuration")
    public @interface Config {
        @AttributeDefinition(name = "MuleSoft Activities rest service url", type = AttributeType.STRING) String restEndPoint();
        @AttributeDefinition(name = "MuleSoft Activities rest service client id", type = AttributeType.STRING) String clientId();
        @AttributeDefinition(name = "MuleSoft Activities rest service client secret", type = AttributeType.STRING) String clientSecret();
    }
}
