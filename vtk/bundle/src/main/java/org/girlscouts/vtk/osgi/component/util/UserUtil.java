package org.girlscouts.vtk.osgi.component.util;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.osgi.component.CouncilMapper;

import javax.servlet.http.HttpServletRequest;

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

}
