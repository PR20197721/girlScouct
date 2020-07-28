package org.girlscouts.vtk.osgi.service.impl;

import org.apache.sling.api.resource.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.mapper.salesforce.*;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.cache.SalesForceContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.SalesForceIRMContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.SalesForceTroopsResponseCache;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceServiceConfig;
import org.girlscouts.vtk.osgi.service.*;
import org.girlscouts.vtk.rest.entity.salesforce.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = {GirlScoutsSalesForceService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSalesForceServiceImpl")
@Designate(ocd = GirlScoutsSalesForceServiceConfig.class)
public class GirlScoutsSalesForceServiceImpl extends BasicGirlScoutsService implements GirlScoutsSalesForceService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsSalesForceServiceImpl.class);
    @Reference
    GirlScoutsSalesForceRestClient sfRestClient;
    @Reference
    GirlScoutsSalesForceFileClient sfFileClient;
    @Reference
    TroopHashGenerator troopHashGenerator;
    @Reference
    SalesForceContactsResponseCache contactsCache;
    @Reference
    SalesForceIRMContactsResponseCache irmContactsCache;
    @Reference
    SalesForceTroopsResponseCache troopsCache;
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
    public ApiConfig getApiConfig(String accessToken) {
        ApiConfig apiConfig = new ApiConfig();
        try {
            JWTAuthEntity jwtAuthEntity = sfRestClient.getJWTAuth(accessToken);
            apiConfig = new ApiConfig();
            apiConfig.setAccessToken(jwtAuthEntity.getAccessToken());
            apiConfig.setInstanceUrl(jwtAuthEntity.getInstanceUrl());
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        StringBuffer token = new StringBuffer();
        return apiConfig;
    }

    @Override
    public User getUser(ApiConfig apiConfig) {
        User user = new User();
        try {
            UserInfoResponseEntity userInfoResponseEntity = null;
            if (apiConfig.isDemoUser() || isLoadFromFile) {
                userInfoResponseEntity = sfFileClient.getUserInfo(apiConfig);
            } else {
                userInfoResponseEntity = sfRestClient.getUserInfo(apiConfig);
            }
            user = UserInfoResponseEntityToUserMapper.map(userInfoResponseEntity);
            if (apiConfig.isDemoUser()) {
                user.setAdminCouncilId(demoCouncilCode);
            }
            user.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
            try {
                user.setTimezone(getUserTimezone(userInfoResponseEntity.getUsers()[0].getContact().getOwner().getCouncilCode()));
            }catch(Exception e){

            }
            apiConfig.setUser(user);
            addMoreInfo(apiConfig, userInfoResponseEntity);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return user;
    }

    @Override
    public User getUserById(ApiConfig apiConfig, String userId) {
        User user = new User();
        try {
            UserInfoResponseEntity userInfoResponseEntity = sfRestClient.getUserInfoById(apiConfig, userId);
            user = UserInfoResponseEntityToUserMapper.map(userInfoResponseEntity);
            user.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
            try {
                user.setTimezone(getUserTimezone(userInfoResponseEntity.getUsers()[0].getContact().getOwner().getCouncilCode()));
            }catch(Exception e){

            }
            apiConfig.setUser(user);
            addMoreInfo(apiConfig, userInfoResponseEntity);
            return user;
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return user;
    }

    @Override
    public List<Troop> getTroopInfoByUserId(ApiConfig apiConfig, String userId) {
        List<Troop> troops = new ArrayList<Troop>();
        try {
            TroopInfoResponseEntity troopInfoResponseEntity = null;
            if (apiConfig.isDemoUser() || isLoadFromFile) {
                troopInfoResponseEntity = sfFileClient.getTroopInfoByUserId(apiConfig, userId);
            } else {
                if(troopsCache.contains(userId)){
                    troopInfoResponseEntity = troopsCache.read(userId);
                }else{
                    troopInfoResponseEntity = sfRestClient.getTroopInfoByUserId(apiConfig, userId);
                    troopsCache.write(userId, troopInfoResponseEntity);
                }
            }
            if (troopInfoResponseEntity != null) {
                TroopEntity[] entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopEntity entity : entities) {
                        Troop troop = TroopEntityToTroopMapper.map(entity);
                        if (apiConfig.isDemoUser()) {
                            troop.setCouncilCode(demoCouncilCode);
                        }
                        troop.setParticipationCode("Troop");
                        troop.setSfUserId(userId);
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
    public List<Contact> getContactsForTroop(ApiConfig apiConfig, Troop troop) {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            ContactsInfoResponseEntity contactsInfoResponseEntity = null;
            if (sumCouncilCode.equals(troop.getCouncilCode())) {
                contactsInfoResponseEntity = sfFileClient.getServiceUnitManagerContacts();
            } else {
                if (apiConfig.isDemoUser() || isLoadFromFile) {
                    contactsInfoResponseEntity = sfFileClient.getContactsByTroopId(apiConfig, troop.getSfTroopId());
                } else {
                    if (troop.getParticipationCode() != null && irmCouncilCode.equals(troop.getParticipationCode())) {
                        if(irmContactsCache.contains(troop.getIrmTroopId())){
                            contactsInfoResponseEntity =  irmContactsCache.read(troop.getIrmTroopId());
                        }else{
                            contactsInfoResponseEntity = sfRestClient.getContactsByTroopId(apiConfig, troop.getIrmTroopId());
                            irmContactsCache.write(troop.getIrmTroopId(), contactsInfoResponseEntity);
                        }
                    } else {
                        if(contactsCache.contains(troop.getSfTroopId())){
                            contactsInfoResponseEntity =  contactsCache.read(troop.getSfTroopId());
                        }else{
                            contactsInfoResponseEntity = sfRestClient.getContactsByTroopId(apiConfig, troop.getSfTroopId());
                            contactsCache.write(troop.getSfTroopId(), contactsInfoResponseEntity);
                        }
                    }
                }
            }
            if (contactsInfoResponseEntity != null) {
                ContactEntity[] entities = contactsInfoResponseEntity.getContacts();
                if (entities != null) {
                    for (ContactEntity entity : entities) {
                        Contact contact = ContactEntityToContactMapper.map(entity);
                        if (troop.getParticipationCode() == null || (troop.getParticipationCode() != null && !irmCouncilCode.equals(troop.getParticipationCode()))){
                            //Not IRM
                            contacts.add(contact);
                        }else{
                            //IRM
                            //Fetching all IRM girls who have current user set as preferred contact to generate dummy troops for parent
                            if(!troop.getIsIRM() && isChildOfParent(contact.getContacts(), apiConfig.getUser().getContactId())){
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
                addRenewals(contacts, contactsInfoResponseEntity);
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

    private void addRenewals(List<Contact> contacts, ContactsInfoResponseEntity contactsInfoResponseEntity) {
        CampaignMemberEntity[] cmEntities = contactsInfoResponseEntity.getCampaignMembers();
        if (cmEntities != null) {
            Map<String, Map> renewals = new TreeMap<>();
            for (CampaignMemberEntity entity : cmEntities) {
                try {
                    String cId = entity.getSfContactId();
                    Map cDetails = renewals.get(cId);
                    if (cDetails == null) {
                        cDetails = new TreeMap<String, Object>();
                    }
                    cDetails.put("Display_Renewal__c", entity.isDisplayRenewal());
                    CampaignMemberEntity.Membership membership = entity.getMembership();
                    if (membership != null) {
                        cDetails.put("Membership__r", membership.getMembershipYear());
                    }
                    renewals.put(cId, cDetails);
                } catch (Exception e) {
                    log.error("Error occured while processing renewals entity: ", e);
                }
            }
            for (Contact contact : contacts) {
                try {
                    Boolean isRenewal = (Boolean) renewals.get(contact.getId()).get("Display_Renewal__c");
                    contact.setRenewalDue(isRenewal == null ? false : isRenewal);
                    Map<String, Object> renewal = renewals.get(contact.getId());
                    if (renewal != null) {
                        Integer membershipYear = (Integer) renewal.get("Membership__r");
                        contact.setMembershipYear(membershipYear);
                    }
                } catch (Exception e) {
                    log.error("Error occured: ", e);
                }
            }
        }
    }

    @Override
    public List<Contact> getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId) {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopLeadersInfoResponseEntity troopLeadersInfoResponseEntity = null;
            if (apiConfig.isDemoUser() || isLoadFromFile) {
                troopLeadersInfoResponseEntity = sfFileClient.getTroopLeaderInfoByTroopId(apiConfig, sfTroopId);
            } else {
                troopLeadersInfoResponseEntity = sfRestClient.getTroopLeaderInfoByTroopId(apiConfig, sfTroopId);
            }
            if (troopLeadersInfoResponseEntity != null) {
                TroopLeaderEntity[] entities = troopLeadersInfoResponseEntity.getTroopLeaders();
                if (entities != null) {
                    for (TroopLeaderEntity entity : entities) {
                        Contact contact = TroopLeaderEntityToContactMapper.map(entity);
                        contacts.add(contact);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return contacts;
    }

    private void addMoreInfo(ApiConfig apiConfig, UserInfoResponseEntity userInfoResponseEntity) {
        if (apiConfig.getUser() != null && userInfoResponseEntity != null) {
            apiConfig.setUserId(apiConfig.getUser().getSfUserId());
            setTroopsForUser(apiConfig, apiConfig.getUser(), userInfoResponseEntity);
            apiConfig.setTroops(apiConfig.getUser().getTroops());
            apiConfig.setUser(apiConfig.getUser());
        }
    }

    private void setTroopsForUser(ApiConfig apiConfig, User user, UserInfoResponseEntity userInfoResponseEntity) {
        List<Troop> parentTroops = new ArrayList<Troop>();
        List<Troop> mergedTroops = new ArrayList<>();
        if(!user.isActive()) {
            mergedTroops = girlScoutsManualTroopLoadService.loadTroops(apiConfig.getUser());
            /*Set<Troop> removeNonRenewedParents = new HashSet<>();
            for (Troop troop : mergedTroops) {
                if ((!user.isActive() && "PA".equals(troop.getRole())) ||troop.getSfTroopName() == null || troop.getRole() == null || troop.getGradeLevel() == null || troop.getCouncilCode() == null || !isValidParticipationCode(troop)) {
                    log.debug("Ignoring troop "+troop.getSfTroopId()+ ". Check all required parameters.");
                    removeNonRenewedParents.add(troop);
                }
            }
            mergedTroops.removeAll(removeNonRenewedParents);*/
        } else {
            ParentEntity[] campsTroops = userInfoResponseEntity.getCamps();
            if (campsTroops != null && campsTroops.length > 0) {
                for (ParentEntity entity : campsTroops) {
                    if (entity.getGradeLevel() != null && entity.getCouncilCode() != null && entity.getParticipationCode() != null && (irmCouncilCode.equals(entity.getParticipationCode()) || "Troop".equals(entity.getParticipationCode()))) {
                        Troop troop = ParentEntityToTroopMapper.map(entity);
                        //Independent Registered Member
                        if (troop.getParticipationCode() != null && irmCouncilCode.equals(troop.getParticipationCode())) {
                            setDummyIRMTroops(apiConfig, user, userInfoResponseEntity, parentTroops, entity, troop);
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
            List<Troop> additionalTroops = getTroopInfoByUserId(apiConfig, user.getSfUserId());
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
        for (Troop troop : mergedTroops) {
            if (apiConfig.isDemoUser()) {
                troop.setCouncilCode(demoCouncilCode);
                troop.setSfCouncil(demoCouncilCode);
            }
            troop.setSfUserId(user.getSfUserId());
            setTroopPermissions(troop, user.isAdmin());
            if(!girlScoutsManualTroopLoadService.isActive() || apiConfig.isDemoUser()) {
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

    private void setDummyIRMTroops(ApiConfig apiConfig, User user, UserInfoResponseEntity userInfoResponseEntity, List<Troop> parentTroops, ParentEntity entity, Troop troop) {
        //Generating dummy troops for each IRM girl under user
        troop.setIrmTroopId(troop.getSfTroopId());
        List<Contact> contacts = getContactsForTroop(apiConfig, troop);
        String parentsCouncilCode = userInfoResponseEntity.getUsers()[0].getContact().getOwner().getCouncilCode();
        for(Contact contact:contacts){
            if(contact != null && "Girl".equals(contact.getRole())){
                Troop dummyIRMTroop = ParentEntityToTroopMapper.map(entity);
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
            TroopInfoResponseEntity troopInfoResponseEntity = sfFileClient.getServiceUnitManagerTroops();
            if (troopInfoResponseEntity != null) {
                TroopEntity[] entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopEntity entity : entities) {
                        Troop troop = TroopEntityToTroopMapper.map(entity);
                        troop.setSfTroopId(sumCouncilCode + "_" + userId);
                        troop.setTroopId(sumCouncilCode + "_" + userId);
                        troop.setId(sumCouncilCode + "_" + userId);
                        troop.setIsSUM(true);
                        troop.setParticipationCode(sumCouncilCode);
                        troop.setSfCouncil(sumCouncilCode);
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