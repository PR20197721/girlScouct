package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.mapper.salesforce.*;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.cache.SalesForceContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.SalesForceIRMContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.SalesForceTroopsResponseCache;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceServiceConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceFileClient;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceRestClient;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService;
import org.girlscouts.vtk.osgi.service.GirlScoutsTroopOCMService;
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

    private String demoCouncilCode;
    private String sumCouncilCode;
    private String irmCouncilCode;
    private Boolean isLoadFromFile;

    @Activate
    private void activate(ComponentContext context) {
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
            user.setApiConfig(apiConfig);
            user.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
            addMoreInfo(apiConfig, userInfoResponseEntity, user);
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
            user.setApiConfig(apiConfig);
            user.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
            addMoreInfo(apiConfig, userInfoResponseEntity, user);
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
                        if(irmContactsCache.contains(troop.getSfTroopId())){
                            contactsInfoResponseEntity =  irmContactsCache.read(troop.getSfTroopId());
                        }else{
                            contactsInfoResponseEntity = sfRestClient.getContactsByTroopId(apiConfig, troop.getSfTroopId());
                            irmContactsCache.write(troop.getSfTroopId(), contactsInfoResponseEntity);
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
                        if (troop.getParticipationCode() == null || (troop.getParticipationCode() != null && !irmCouncilCode.equals(troop.getParticipationCode())) || (troop.getParticipationCode() != null && irmCouncilCode.equals(troop.getParticipationCode()) && entity.getAccount().getSfPreferredContactId().equals(apiConfig.getUser().getContactId()))) {
                            Contact contact = ContactEntityToContactMapper.map(entity);
                            contacts.add(contact);
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

    private void addMoreInfo(ApiConfig apiConfig, UserInfoResponseEntity userInfoResponseEntity, User user) {
        if (user != null && userInfoResponseEntity != null) {
            ParentEntity[] campsTroops = userInfoResponseEntity.getCamps();
            setTroopsForUser(apiConfig, user, userInfoResponseEntity, campsTroops);
            apiConfig.setTroops(user.getTroops());
            apiConfig.setUserId(user.getSfUserId());
            apiConfig.setUser(user);
            user.setApiConfig(apiConfig);
        }
    }

    private void setTroopsForUser(ApiConfig apiConfig, User user, UserInfoResponseEntity userInfoResponseEntity, ParentEntity[] campsTroops) {
        List<Troop> parentTroops = new ArrayList<Troop>();
        if (campsTroops != null && campsTroops.length > 0) {
            for (ParentEntity entity : campsTroops) {
                Troop troop = ParentEntityToTroopMapper.map(entity);
                //Independent Registered Member
                if (troop.getParticipationCode() != null && irmCouncilCode.equals(troop.getParticipationCode())) {
                    try {
                        String parentsCouncilCode = userInfoResponseEntity.getUsers()[0].getContact().getOwner().getCouncilCode();
                        troop.setCouncilCode(parentsCouncilCode);
                        troop.setSfCouncil(parentsCouncilCode);
                    }catch(Exception e){
                        log.error("Could not retrieve a council code for IRM member "+user.getSfUserId()+ " from Salesforce json response");
                        //default
                        troop.setCouncilCode(irmCouncilCode);
                        troop.setSfCouncil(irmCouncilCode);
                    }
                    troop.setIrmTroopId(troop.getSfTroopId());
                    //parent is used as troop
                    troop.setSfTroopId(irmCouncilCode + "_" + user.getSfUserId());
                    troop.setTroopId(irmCouncilCode + "_" + user.getSfUserId());
                    troop.setId(irmCouncilCode + "_" + user.getSfUserId());
                }
                troop.setRole("PA");
                parentTroops.add(troop);
            }
        }
        List<Troop> additionalTroops = getTroopInfoByUserId(apiConfig, user.getSfUserId());
        //Service Unit Manager
        if (user.isServiceUnitManager()) {
            additionalTroops.addAll(getServiceUnitManagerTroops(user.getSfUserId()));
        }
        List<Troop> mergedTroops = mergeTroops(parentTroops, additionalTroops);
        for (Troop troop : mergedTroops) {
            if (apiConfig.isDemoUser()) {
                troop.setCouncilCode(demoCouncilCode);
                troop.setSfCouncil(demoCouncilCode);
            }
            troop.setSfUserId(user.getSfUserId());
            setTroopPermissions(troop, user.isAdmin());
            setTroopPaths(troop);
            troop.setHash(troopHashGenerator.hash(troop.getPath()));
            if(girlScoutsTroopOCMService.read(troop.getPath()) == null){
                girlScoutsTroopOCMService.create(troop);
            }
        }
        user.setTroops(mergedTroops);
    }

    private void setTroopPaths(Troop troop) {
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
}
