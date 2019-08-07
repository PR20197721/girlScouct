package org.girlscouts.vtk.osgi.component.util;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.osgi.component.CouncilMapper;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import java.io.DataOutputStream;
import java.net.URL;

@Component
@Service(value = UserUtil.class)
public class UserUtil {
    @Reference
    private CouncilMapper councilMapper;

    public boolean hasPermission(java.util.Set<Integer> myPermissionTokens, int permissionId) {
        return myPermissionTokens != null && myPermissionTokens.contains(permissionId);

    }

    public boolean hasPermission(Troop troop, int permissionId) {
        return troop != null && troop.getPermissionTokens() != null && hasPermission(troop.getPermissionTokens(), permissionId);
    }

    public String getCouncilUrlPath(ApiConfig apiConfig, HttpServletRequest request) {
        String redirectUrl = null;
        try {
            String councilId = apiConfig.getUser().getTroops().get(0).getCouncilCode();
            if (councilId == null || councilId.trim().equals("")) {
                redirectUrl = councilMapper.getCouncilUrl(VtkUtil.getCouncilInClient(request));
            } else {
                redirectUrl = councilMapper.getCouncilUrl(councilId);
            }
        } catch (Exception e) {
        }
        return redirectUrl;
    }

    // aPI logout
    public boolean logoutApi(ApiConfig apiConfig, boolean isRefreshToken) throws Exception {
        DataOutputStream wr = null;
        boolean isSucc = false;
        URL obj = null;
        HttpsURLConnection con = null;
        try {
            String url = apiConfig.getInstanceUrl() + "/services/oauth2/revoke";
            obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String urlParameters = "token=" + apiConfig.getAccessToken();
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                isSucc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                wr = null;
                obj = null;
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isSucc;
    }

}
