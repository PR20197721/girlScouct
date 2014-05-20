package org.girlscouts.vtk.auth.dao;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;

public class SalesForceDAO {
    public static User getUser(ApiConfig config) {
        User user = new User();

        List accounts = new ArrayList();
        try {
            // -String instanceUrl="https://na15.salesforce.com";
            HttpClient httpclient = new HttpClient();
            GetMethod get = new GetMethod(config.getInstanceUrl()
                    + "/services/data/v20.0/query");

            // set the token in the header
            get.setRequestHeader("Authorization",
                    "OAuth " + config.getAccessToken());

            // set the SOQL as a query param
            NameValuePair[] params = new NameValuePair[1];

            params[0] = new NameValuePair("q",
                    "SELECT ID,name,email from User where id='"
                            + config.getUserId() + "' limit 1");
            get.setQueryString(params);

            try {
                httpclient.executeMethod(get);

                System.err.println(get.getStatusCode() + " : "
                        + get.getResponseBodyAsString());
                if (get.getStatusCode() == HttpStatus.SC_OK) {
                    // Now lets use the standard java json classes to work with
                    // the
                    // results
                    try {
                        JSONObject response = new JSONObject(new JSONTokener(
                                new InputStreamReader(
                                        get.getResponseBodyAsStream())));
                        System.out.println("Query response: "
                                + response.toString(2));
                        // System.err.println("display_name="+response.getString("display_Name"));
                        // user.setDisplayName(response.getString("display_Name"));

                        JSONArray results = response.getJSONArray("records");

                        for (int i = 0; i < results.length(); i++) {
                            // System.err.println(results.getJSONObject(i).getString("Name"));
                            user.setName(results.getJSONObject(i).getString(
                                    "Name"));
                            user.setEmail(results.getJSONObject(i).getString(
                                    "Email"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            } finally {
                get.releaseConnection();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return user;

    }
}
