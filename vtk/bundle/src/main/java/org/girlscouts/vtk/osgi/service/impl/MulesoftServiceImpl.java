package org.girlscouts.vtk.osgi.service.impl;

import org.apache.sling.api.resource.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.mapper.mulesoft.*;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.cache.MulesoftContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.MulesoftTroopsResponseCache;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.conf.MulesoftServiceConfig;
import org.girlscouts.vtk.osgi.service.*;
import org.girlscouts.vtk.rest.entity.mulesoft.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = {MulesoftService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.MulesoftServiceImpl")
@Designate(ocd = MulesoftServiceConfig.class)
public class MulesoftServiceImpl extends BasicGirlScoutsService implements MulesoftService {
    private static Logger log = LoggerFactory.getLogger(MulesoftServiceImpl.class);
    @Reference
    MulesoftRestClient restClient;
    @Reference
    MulesoftFileClient fileClient;
    @Reference
    TroopHashGenerator troopHashGenerator;
    @Reference
    MulesoftContactsResponseCache contactsCache;
    @Reference
    MulesoftTroopsResponseCache troopsCache;
    @Reference
    GirlScoutsTroopOCMService girlScoutsTroopOCMService;
    @Reference
    CouncilMapper councilMapper;
    @Reference
    ResourceResolverFactory resolverFactory;
    @Reference
    GirlScoutsManualTroopLoadService girlScoutsManualTroopLoadService;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();


    private String demoCouncilCode;
    private String sumCouncilCode;
    private String irmCouncilCode;
    private Boolean isLoadFromFile;

    @Activate
    private void activate(ComponentContext context) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        this.context = context;
        this.isLoadFromFile = Boolean.parseBoolean(getConfig("isLoadFromFile"));
        this.sumCouncilCode = getConfig("sumCouncilCode");
        this.irmCouncilCode = getConfig("irmCouncilCode");
        this.demoCouncilCode = getConfig("demoCouncilCode");
        log.info("Girl Scouts VTK SalesForce Service Activated.");
    }

    @Override
    public User getUser(org.apache.jackrabbit.api.security.user.User user) {
        User vtkUser = new User();
        try {
            String gsGlobalId = user.getProperty("./profile/GSGlobalID")!=null ? user.getProperty("./profile/GSGlobalID")[0].getString() : null;
            String email = user.getProperty("./profile/email") != null ? user.getProperty("./profile/email")[0].getString() : null;
            if(gsGlobalId != null){
                Boolean isDemo = false;
                if(email != null && email.endsWith("@vtkdemo.girlscouts.org")){
                    isDemo = true;
                }
                UserInfoResponseEntity userInfoResponseEntity = null;
                if (isLoadFromFile || isDemo) {
                    userInfoResponseEntity = fileClient.getUser(gsGlobalId, isDemo);
                } else {
                    userInfoResponseEntity = restClient.getUser(gsGlobalId);
                }
                vtkUser = UserInfoResponseEntityToUserMapper.map(userInfoResponseEntity);
                if (isDemo) {
                    vtkUser.setAdminCouncilId(demoCouncilCode);
                }
                vtkUser.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
                try {
                    vtkUser.setTimezone(getUserTimezone(vtkUser.getAdminCouncilId()));
                }catch(Exception e){

                }
                setTroopsForUser(vtkUser, userInfoResponseEntity);
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return vtkUser;
    }

    @Override
    public List<Troop> getTroops(User user) {
        List<Troop> troops = new ArrayList<Troop>();
        try {
            TroopInfoResponseEntity troopInfoResponseEntity = null;
            if (user.isDemo() || isLoadFromFile) {
                troopInfoResponseEntity = fileClient.getTroops(user.getSfUserId(), user.isDemo());
            } else {
                if(troopsCache.contains(user.getSfUserId())){
                    troopInfoResponseEntity = troopsCache.read(user.getSfUserId());
                }else{
                    troopInfoResponseEntity = restClient.getTroops(user.getSfUserId());
                    troopsCache.write(user.getSfUserId(), troopInfoResponseEntity);
                }
            }
            if (troopInfoResponseEntity != null) {
                List<TroopEntity> entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopEntity entity : entities) {
                        Troop troop = TroopEntityToTroopMapper.map(entity);
                        if (user.isDemo()) {
                            troop.setCouncilCode(demoCouncilCode);
                        }
                        troop.setParticipationCode("Troop");
                        troop.setSfUserId(user.getSfUserId());
                        setTroopPath(troop);
                        troops.add(troop);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return troops;
    }

    @Override
    public List<Contact> getContactsForTroop(Troop troop) {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopMembersResponseEntity troopMembersResponseEntity = null;
            if (sumCouncilCode.equals(troop.getCouncilCode())) {
                troopMembersResponseEntity = fileClient.getServiceUnitManagerContacts();
            } else {
                if (troop.getIsDemoTroop() || isLoadFromFile) {
                    troopMembersResponseEntity = fileClient.getMembers(troop.getSfTroopId(), troop.getIsDemoTroop());
                } else {
                    if(contactsCache.contains(troop.getSfTroopId())){
                        troopMembersResponseEntity =  contactsCache.read(troop.getSfTroopId());
                    }else{
                        troopMembersResponseEntity = restClient.getMembers(troop.getSfTroopId());
                        contactsCache.write(troop.getSfTroopId(), troopMembersResponseEntity);
                    }
                }
            }
            if (troopMembersResponseEntity != null) {
                List<MembersEntity> entities = troopMembersResponseEntity.getMembers();
                if (entities != null) {
                    for (MembersEntity entity : entities) {
                        Contact contact = TroopMemberEntityToContactMapper.map(entity, troopMembersResponseEntity.getTroop());
                        if (troop.getParticipationCode() == null || (troop.getParticipationCode() != null && !irmCouncilCode.equals(troop.getParticipationCode()))){
                            //Not IRM
                            contacts.add(contact);
                        }else{
                            //IRM
                            //Fetching all IRM girls who have current user set as preferred contact to generate dummy troops for parent
                            if(!troop.getIsIRM() && isChildOfParent(contact.getContacts(), troop.getSfUserId())){
                                contacts.add(contact);
                            }else{
                                //Fetching specific girl info for her dummy troop?
                                if(troop.getSfTroopId().equals(irmCouncilCode+"_"+contact.getId())) {
                                    contacts.add(contact);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return contacts;
    }

    private boolean isChildOfParent(List<Contact> girlsContacts, String parentId) {
        if(girlsContacts != null && parentId != null){
            for(Contact contact:girlsContacts){
                try {
                    if(contact.getId().equals(parentId)){
                        return true;
                    }
                }catch(Exception e){
                    log.error("Error occurred:", e);
                }
            }
        }
        return false;
    }


    @Override
    public List<Contact> getTroopLeaders(Troop troop) {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopLeadersResponseEntity troopLeadersInfoResponseEntity = null;
            if (troop.getIsDemoTroop() || isLoadFromFile) {
                troopLeadersInfoResponseEntity = fileClient.getTroopLeaders(troop.getSfTroopId(), troop.getIsDemoTroop());
            } else {
                troopLeadersInfoResponseEntity = restClient.getTroopLeaders(troop.getSfTroopId());
            }
            if (troopLeadersInfoResponseEntity != null) {
                List<DPInfoEntity> entities = troopLeadersInfoResponseEntity.getTroopLeaders();
                if (entities != null) {
                    for (DPInfoEntity entity : entities) {
                        Contact contact = DPInfoEntityToContactMapper.map(entity);
                        contacts.add(contact);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return contacts;
    }

    @Override
    public ApiConfig getApiConfig(org.apache.jackrabbit.api.security.user.User user) {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setDemoUser(false);
        try {
            String gsGlobalId = user.getProperty("./profile/GSGlobalID") != null ? user.getProperty("./profile/GSGlobalID")[0].getString() : null;
            String email = user.getProperty("./profile/email") != null ? user.getProperty("./profile/email")[0].getString() : null;
            if (gsGlobalId != null) {
                if (email != null && email.endsWith("@vtkdemo.girlscouts.org")) {
                    apiConfig.setDemoUser(true);
                }
            }
            User vtkUser = getUser(user);
            apiConfig.setUser(vtkUser);
            apiConfig.setTroops(vtkUser.getTroops());
            apiConfig.setUserId(vtkUser.getSfUserId());
        }catch(Exception e){

        }
        return apiConfig;
    }

    private void setTroopsForUser(User user, UserInfoResponseEntity userInfoResponseEntity) {
        List<Troop> parentTroops = new ArrayList<Troop>();
        List<Troop> mergedTroops = new ArrayList<>();
        if(!user.isActive()) {
            mergedTroops = girlScoutsManualTroopLoadService.loadTroops(user);
        } else {
            List<AffiliationsEntity> affiliations = userInfoResponseEntity.getAffiliations();
            if (affiliations != null && affiliations.size() > 0) {
                for (AffiliationsEntity entity : affiliations) {
                    if (entity.getProgramGradeLevel() != null && entity.getCouncilCode() != null && entity.getType() != null && ("IRG".equals(entity.getType()) || "Troop".equals(entity.getType()))) {
                        Troop troop = AffiliationsEntityToTroopMapper.map(entity);
                        //Independent Registered Member
                        if (troop.getParticipationCode() != null && "IRG".equals(troop.getParticipationCode())) {
                            setDummyIRMTroops(user, userInfoResponseEntity, parentTroops, entity, troop);
                        } else {
                            troop.setRole("PA");
                            setTroopPath(troop);
                            parentTroops.add(troop);
                        }
                        troop.setSfUserId(user.getSfUserId());
                    } else {
                        log.debug("Skipping parent troop: {}", entity.toString());
                    }
                }
            }
            List<Troop> additionalTroops = getTroops(user);
            mergedTroops = mergeTroops(parentTroops, additionalTroops);
        }
        //Service Unit Manager
        if (user.isServiceUnitManager()) {
            mergedTroops.addAll(getServiceUnitManagerTroops(user.getSfUserId()));
        }
        Set<Troop> invalidTroops = new HashSet<>();
        for (Troop troop : mergedTroops) {
            if (troop.getSfTroopName() == null || troop.getRole() == null || troop.getGradeLevel() == null || troop.getCouncilCode() == null || !isValidParticipationCode(troop)) {
                log.debug("Ignoring troop "+troop.getSfTroopId()+ ". Check all required parameters.");
                invalidTroops.add(troop);
            }
        }
        mergedTroops.removeAll(invalidTroops);
        if((mergedTroops == null || mergedTroops.size() == 0) && user.isAdmin()){
            Troop dummyVTKAdminTroop = new Troop();
            dummyVTKAdminTroop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
            dummyVTKAdminTroop.setTroopId("none");
            dummyVTKAdminTroop.setSfTroopName("vtk_virtual_troop");
            dummyVTKAdminTroop.setSfCouncil(user.getAdminCouncilId());
            dummyVTKAdminTroop.setSfUserId("none");
            dummyVTKAdminTroop.setSfTroopId("none");
            dummyVTKAdminTroop.setCouncilCode(user.getAdminCouncilId());
            dummyVTKAdminTroop.setTroopName("vtk_virtual_troop");
            dummyVTKAdminTroop.setGradeLevel("CA");
            String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + dummyVTKAdminTroop.getSfCouncil();
            dummyVTKAdminTroop.setCouncilPath(councilPath);
            String troopPath = councilPath + "/troops/" + dummyVTKAdminTroop.getSfTroopId();
            dummyVTKAdminTroop.setPath(troopPath);
            mergedTroops.add(dummyVTKAdminTroop);
        }
        for (Troop troop : mergedTroops) {
            if (user.isDemo()) {
                troop.setCouncilCode(demoCouncilCode);
                troop.setSfCouncil(demoCouncilCode);
                troop.setIsDemoTroop(true);
            }else{
                troop.setIsDemoTroop(false);
            }
            troop.setSfUserId(user.getSfUserId());
            setTroopPermissions(troop, user.isAdmin());
            if(!girlScoutsManualTroopLoadService.isActive() || user.isDemo()) {
                setTroopPath(troop);
            }
            troop.setHash(troopHashGenerator.hash(troop.getPath()));
            if(girlScoutsTroopOCMService.read(troop.getPath()) == null){
                girlScoutsTroopOCMService.create(troop);
            }
        }
        user.setTroops(mergedTroops);
    }

    private boolean isValidParticipationCode(Troop troop) {
        return troop.getParticipationCode() != null && (irmCouncilCode.equals(troop.getParticipationCode()) || sumCouncilCode.equals(troop.getParticipationCode()) || "Troop".equals(troop.getParticipationCode()));
    }

    private void setDummyIRMTroops(User user, UserInfoResponseEntity userInfoResponseEntity, List<Troop> parentTroops, AffiliationsEntity entity, Troop troop) {
        //Generating dummy troops for each IRM girl under user
        troop.setIrmTroopId(troop.getSfTroopId());
        List<Contact> contacts = getContactsForTroop(troop);
        String parentsCouncilCode = user.getAdminCouncilId();
        for(Contact contact:contacts){
            if(contact != null && "Girl".equals(contact.getRole())){
                Troop dummyIRMTroop = AffiliationsEntityToTroopMapper.map(entity);
                try {
                    dummyIRMTroop.setCouncilCode(parentsCouncilCode);
                    dummyIRMTroop.setSfCouncil(parentsCouncilCode);
                }catch(Exception e){
                    log.error("Could not retrieve a council code for IRM member "+user.getSfUserId()+ " from Salesforce json response");
                    //default
                    dummyIRMTroop.setCouncilCode(irmCouncilCode);
                    dummyIRMTroop.setSfCouncil(irmCouncilCode);
                }
                dummyIRMTroop.setParticipationCode(irmCouncilCode);
                dummyIRMTroop.setIrmTroopId(troop.getSfTroopId());
                //parent is used as troop
                dummyIRMTroop.setSfTroopId(irmCouncilCode + "_" +contact.getId());
                dummyIRMTroop.setTroopId(irmCouncilCode + "_" + contact.getId());
                dummyIRMTroop.setId(irmCouncilCode + "_" + contact.getId());
                String troopName = "";
                if(contact.getFirstName() != null){
                    troopName = contact.getFirstName();
                }
                if(contact.getLastName() != null){
                    troopName += contact.getLastName();
                }
                dummyIRMTroop.setTroopName(troopName);
                dummyIRMTroop.setSfTroopName(troopName);
                dummyIRMTroop.setRole("PA");
                dummyIRMTroop.setGradeLevel(troop.getGradeLevel());
                dummyIRMTroop.setIsIRM(true);
                dummyIRMTroop.setIsDemoTroop(false);
                setTroopPath(dummyIRMTroop);
                parentTroops.add(dummyIRMTroop);
            }
        }
    }

    private void setTroopPath(Troop troop) {
        String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + troop.getSfCouncil();
        troop.setCouncilPath(councilPath);
        String troopPath = councilPath + "/troops/" + troop.getSfTroopId();
        troop.setPath(troopPath);
    }

    private List<Troop> getServiceUnitManagerTroops(String userId) {
        List<Troop> troops = new ArrayList<Troop>();
        try {
            TroopInfoResponseEntity troopInfoResponseEntity = fileClient.getServiceUnitManagerTroops();
            if (troopInfoResponseEntity != null) {
                List<TroopEntity> entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopEntity entity : entities) {
                        Troop troop = TroopEntityToTroopMapper.map(entity);
                        troop.setSfTroopId(sumCouncilCode + "_" + userId);
                        troop.setTroopId(sumCouncilCode + "_" + userId);
                        troop.setId(sumCouncilCode + "_" + userId);
                        troop.setIsSUM(true);
                        troop.setParticipationCode(sumCouncilCode);
                        troop.setSfCouncil(sumCouncilCode);
                        troop.setIsDemoTroop(false);
                        troop.setIsIRM(false);
                        setTroopPath(troop);
                        troops.add(troop);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return troops;
    }

    private void setTroopPermissions(Troop troop, boolean isAdmin) {
        String roleType = troop.getRole();
        troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
        if ("PA".equals(roleType)) {
            troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
        }
        if ("DP".equals(roleType) || (troop.getParticipationCode() != null && troop.getParticipationCode().equals(irmCouncilCode)) || troop.getCouncilCode().equals(sumCouncilCode)) {
            troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
        }
        if (isAdmin) {
            troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
        }
    }

    private List<Troop> mergeTroops(List<Troop> A, List<Troop> B) {
        if ((A == null && B == null) || (A.size() <= 0 && B.size() <= 0)) {
            return new ArrayList();
        }
        if ((A == null || A.size() <= 0) && (B != null)) {
            return B;
        }
        if ((B == null || B.size() <= 0) && A != null) {
            return A;
        }
        List<Troop> troopDiff = getTroopsNotInA(A, B);
        A.addAll(troopDiff);
        for (int i = 0; i < A.size(); i++) {
            Troop troop = A.get(i);
            for (int y = 0; y < B.size(); y++) {
                Troop _troop = B.get(y);
                if (_troop.getTroopId().equals(troop.getTroopId())) {
                    try {
                        if ("DP".equals(_troop.getRole()) && "PA".equals(troop.getRole())) {
                            troop.setRole("DP");
                        }
                    } catch (Exception e) {
                        log.error("Error occurred while merging troops for user ", e);
                    }
                    try {
                        if (troop.getPermissionTokens() == null) {
                            troop.setPermissionTokens(new HashSet<Integer>());
                        }
                        troop.getPermissionTokens().addAll(_troop.getPermissionTokens());
                    } catch (Exception e) {
                        log.error("Error occurred while merging permissions for troops " + troop.getSfTroopId() + " and " + _troop.getSfTroopId(), e);
                    }
                }
            }
        }
        return A;
    }

    private List<Troop> getTroopsNotInA(List<Troop> A, List<Troop> B) {
        List<Troop> troopDiff = new ArrayList();
        for (int i = 0; i < B.size(); i++) {
            Troop troop = B.get(i);
            boolean isFound = false;
            for (int y = 0; y < A.size(); y++) {
                Troop _troop = A.get(y);
                if (_troop.getTroopId().equals(troop.getTroopId())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                troopDiff.add(troop);
            }
        }
        return troopDiff;
    }

    private String getUserTimezone(String councilCode){
        log.debug("Looking up timezone for "+councilCode);
        String timezone = "";
        String councilSite = councilMapper.getCouncilBranch(councilCode);
        if(councilSite != null && councilSite.trim().length() > 0) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource councilSiteRes = rr.resolve(councilSite+"/en/jcr:content");
                if(councilSiteRes != null && !ResourceUtil.isNonExistingResource(councilSiteRes)){
                    ValueMap vm = councilSiteRes.adaptTo(ValueMap.class);
                    if(vm.containsKey("timezone")){
                        timezone = vm.get("timezone",String.class);
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        }
        return timezone;
    }
}