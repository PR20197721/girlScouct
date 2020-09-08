package org.girlscouts.vtk.osgi.service.impl;

import org.apache.commons.lang.StringUtils;
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
import org.girlscouts.vtk.osgi.service.*;
import org.girlscouts.vtk.rest.entity.mulesoft.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = {MulesoftService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.MulesoftServiceImpl")
@Designate(ocd = MulesoftServiceImpl.Config.class)
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
    public ApiConfig getApiConfig(org.apache.jackrabbit.api.security.user.User user) {
        log.debug("Getting apiConfig");
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
            log.error("Exception occured",e);
        }
        return apiConfig;
    }

    @Override
    public User getUser(org.apache.jackrabbit.api.security.user.User user) {
        log.debug("Getting user details for "+user);
        User vtkUser = new User();
        try {
            String gsGlobalId = user.getProperty("./profile/GSGlobalID")!=null ? user.getProperty("./profile/GSGlobalID")[0].getString() : null;
            String email = user.getProperty("./profile/email") != null ? user.getProperty("./profile/email")[0].getString() : null;
            log.debug("gsGlobalId="+gsGlobalId +" email="+email);
            if(gsGlobalId != null){
                Boolean isDemo = false;
                if(email != null && email.endsWith("@vtkdemo.girlscouts.org")){
                    isDemo = true;
                }
                UserInfoResponseEntity userInfoResponseEntity = null;
                if (isLoadFromFile || isDemo) {
                    log.debug("loading user from file");
                    userInfoResponseEntity = fileClient.getUser(gsGlobalId, isDemo);
                } else {
                    log.debug("getting user details from mulesoft");
                    userInfoResponseEntity = restClient.getUser(gsGlobalId);
                }
                log.debug("User Entity:"+userInfoResponseEntity);
                vtkUser = UserInfoResponseEntityToUserMapper.map(userInfoResponseEntity);
                if (isDemo) {
                    log.debug("User is a demo user");
                    vtkUser.setAdminCouncilId(demoCouncilCode);
                }
                vtkUser.setCurrentYear(String.valueOf(VtkUtil.getCurrentGSYear()));
                try {
                    vtkUser.setTimezone(getUserTimezone(vtkUser.getAdminCouncilId()));
                }catch(Exception e){

                }
                log.debug("Generated user "+vtkUser);
                setTroopsForUser(vtkUser, userInfoResponseEntity);
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return vtkUser;
    }

    @Override
    public List<Troop> getTroops(User user) {
        log.debug("Getting troop details for "+user);
        List<Troop> troops = new ArrayList<Troop>();
        try {
            TroopInfoResponseEntity troopInfoResponseEntity = null;
            if (user.isDemo() || isLoadFromFile) {
                log.debug("Getting demo troop details ");
                troopInfoResponseEntity = fileClient.getTroops(user.getSfUserId(), user.isDemo());
            } else {
                if(troopsCache.contains(user.getSfUserId())){
                    log.debug("Getting troop details from cache");
                    troopInfoResponseEntity = troopsCache.read(user.getSfUserId());
                }else{
                    log.debug("Getting troop details from mulesoft");
                    troopInfoResponseEntity = restClient.getTroops(user.getSfUserId());
                    troopsCache.write(user.getSfUserId(), troopInfoResponseEntity);
                }
            }
            log.debug("Troop Info Entity:"+troopInfoResponseEntity);
            if (troopInfoResponseEntity != null) {
                List<TroopWrapperEntity> entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopWrapperEntity entity : entities) {
                        try {
                            Troop troop = TroopEntityToTroopMapper.map(entity.getTroop());
                            if (user.isDemo()) {
                                troop.setCouncilCode(demoCouncilCode);
                            }
                            troop.setParticipationCode("Troop");
                            troop.setSfUserId(user.getSfUserId());
                            setTroopPath(troop);
                            log.debug("Adding troop: " + troop);
                            setTroopPermissions(troop);
                            if(!StringUtils.isBlank(troop.getRole()) && "DP".equals(troop.getRole())){
                                troops.add(troop);
                            }
                        }catch(Exception e){
                            log.error("Error occurred: ", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return troops;
    }

    @Override
    public List<Contact> getContactsForTroop(Troop troop, User user) {
        log.debug("Getting contacts for troop "+troop);
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
            log.debug("Troop Contacts Entity:"+troopMembersResponseEntity);
            if (troopMembersResponseEntity != null) {
                List<MembersEntity> entities = troopMembersResponseEntity.getMembers();
                if (entities != null) {
                    for (MembersEntity entity : entities) {
                        Contact contact = TroopMemberEntityToContactMapper.map(entity, troopMembersResponseEntity.getTroop());
                        if (troop.getParticipationCode() == null || (troop.getParticipationCode() != null && !"IRM".equals(troop.getParticipationCode()))){
                            //Not IRM
                            log.debug("found non IRM contact : "+contact.getId());
                            contacts.add(contact);
                        }else{
                            //Fetching specific girl info for her dummy troop?
                            if(troop.getSfTroopId().equals(irmCouncilCode+"_"+contact.getId())) {
                                log.debug("found IRM contact : "+contact.getId());
                                contacts.add(contact);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        log.debug("returned "+contacts.size()+" contacts");
        return contacts;
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
            log.debug("Troop Leaders Info Entity:"+troopLeadersInfoResponseEntity);
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



    public List<Contact> getContactsForIRMParent(String accountId, User user) {
        log.debug("Getting contacts for Account Id: "+accountId);
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopMembersResponseEntity troopMembersResponseEntity = null;
            if (isLoadFromFile) {
                troopMembersResponseEntity = fileClient.getMembers(accountId, false);
            } else {
                if(contactsCache.contains(accountId)){
                    troopMembersResponseEntity =  contactsCache.read(accountId);
                }else{
                    troopMembersResponseEntity = restClient.getMembers(accountId);
                    contactsCache.write(accountId, troopMembersResponseEntity);
                }
            }
            log.debug("Troop Contacts Entity:"+troopMembersResponseEntity);
            if (troopMembersResponseEntity != null) {
                List<MembersEntity> entities = troopMembersResponseEntity.getMembers();
                if (entities != null) {
                    for (MembersEntity entity : entities) {
                        Contact contact = TroopMemberEntityToContactMapper.map(entity, troopMembersResponseEntity.getTroop());
                        //IRM
                        //Fetching all IRM girls who have current user set as primary guardian to generate IRM troops for parent
                        if(isChildOfUser(contact, user)){
                            log.debug("found girl for IRM parent: "+contact);
                            contacts.add(contact);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        log.debug("returned "+contacts.size()+" contacts");
        return contacts;
    }

    private boolean isChildOfUser(Contact contact, User user) {
        boolean result = false;
        if(contact != null && contact.getRole() != null && "Girl".equals(contact.getRole()) && contact.getPrimaryGuardian() != null && contact.getPrimaryGuardian().getEmail() != null){
            String userEmail = user.getEmail();
            String primaryGuardianEmail = contact.getPrimaryGuardian().getEmail();
            if(userEmail.equals(primaryGuardianEmail)){
                result = true;
            }
        }
        return result;
    }


    private void setTroopsForUser(User user, UserInfoResponseEntity userInfoResponseEntity) {
        List<Troop> parentTroops = getParentTroops(user, userInfoResponseEntity);
        List<Troop> additionalTroops = getTroops(user);
        List<Troop> mergedTroops = mergeTroops(parentTroops, additionalTroops);
        //Service Unit Manager
        if (user.isServiceUnitManager()) {
            mergedTroops.addAll(buildServiceUnitManagerTroops(user));
        }
        //VTK Admin
        if(user.isAdmin()){
            mergedTroops.add(buildVTKAdminTroop(user));
        }
        validateTroops(mergedTroops);
        for (Troop troop : mergedTroops) {
            if (user.isDemo()) {
                troop.setCouncilCode(demoCouncilCode);
                troop.setSfCouncil(demoCouncilCode);
                troop.setIsDemoTroop(true);
            }
            troop.setHash(troopHashGenerator.hash(troop.getPath()));
            if(girlScoutsTroopOCMService.read(troop.getPath()) == null && !troop.getIsTransient()){
                girlScoutsTroopOCMService.create(troop);
            }
        }
        log.debug("final list of troops: "+mergedTroops);
        user.setTroops(mergedTroops);
    }



    private List<Troop> getParentTroops(User user, UserInfoResponseEntity userInfoResponseEntity) {
        List<Troop> parentTroops = new ArrayList<Troop>();
        try {
            List<AffiliationsEntity> affiliations = userInfoResponseEntity.getAffiliations();
            if (affiliations != null && affiliations.size() > 0) {
                for (AffiliationsEntity entity : affiliations) {
                    log.debug("Affiliation:" + entity);
                    Troop troop = AffiliationsEntityToTroopMapper.map(entity);
                    troop.setSfUserId(user.getSfUserId());
                    if (troop.getStartDate() != null && troop.getEndDate() != null) {
                        if (troop.getStartDate().isBeforeNow() && troop.getEndDate().isAfterNow()) {
                            if (troop.getParticipationCode() != null && ("IRG".equals(troop.getParticipationCode()) || "Troop".equals(troop.getParticipationCode()))) {
                                if (troop.getGradeLevel() != null || "IRG".equals(troop.getParticipationCode())) {
                                    if (troop.getCouncilCode() != null) {
                                        if ("IRG".equals(troop.getParticipationCode())) {
                                            log.debug("IRG Affiliation:" + entity);
                                            parentTroops.addAll(getIRMTroops(user, troop.getSfTroopId()));
                                        } else {
                                            log.debug("Parent Affiliation:" + entity);
                                            setTroopPath(troop);
                                            setTroopPermissions(troop);
                                            log.debug("adding parent troop" + troop);
                                            parentTroops.add(troop);
                                        }
                                    } else {
                                        log.debug("Ignoring parent troop: {} due to invalid value for council code = {}", troop, troop.getCouncilCode());
                                    }
                                } else {
                                    log.debug("Ignoring parent troop: {} due to invalid value for grade level = {}", troop, troop.getGradeLevel());
                                }
                            } else {
                                log.debug("Ignoring parent troop: {} due to invalid value for participation code {}", troop, troop.getParticipationCode());
                            }
                        } else {
                            log.debug("Ignoring parent troop: {} due to current date is not within troop startDate = {} and endDate = {}", troop, troop.getStartDate(), troop.getEndDate());
                        }
                    } else {
                        log.debug("Ignoring parent troop: {} due to null values for startDate = {} or endDate = {}", troop, troop.getStartDate(), troop.getEndDate());
                    }
                }
            }
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return parentTroops;
    }

    private List<Troop> getIRMTroops(User user, String accountId) {
        List<Troop> irmTroops = new ArrayList<Troop>();
        try {
            //Generating dummy troops for each IRM girl under user
            List<Contact> contacts = getContactsForIRMParent(accountId, user);
            for (Contact contact : contacts) {
                if (contact != null && "Girl".equals(contact.getRole()) && isChildOfUser(contact, user)) {
                    Troop irmTroop = new Troop();
                    irmTroop.setRole("PA");
                    irmTroop.setSfTroopId(irmCouncilCode + "_" + contact.getId());
                    irmTroop.setTroopId(irmCouncilCode + "_" + contact.getId());
                    irmTroop.setId(irmCouncilCode + "_" + contact.getId());
                    irmTroop.setParticipationCode(irmCouncilCode);
                    String troopName = "";
                    if (contact.getFirstName() != null) {
                        troopName = contact.getFirstName();
                    }
                    if (contact.getLastName() != null) {
                        troopName += contact.getLastName();
                    }
                    irmTroop.setTroopName(troopName);
                    irmTroop.setSfTroopName(troopName);

                    irmTroop.setGradeLevel("7-Multi-level");
                    irmTroop.setIsIRM(true);
                    irmTroop.setIsDemoTroop(false);
                    setTroopPath(irmTroop);
                    log.debug("adding IRM troop " + irmTroop);
                    setTroopPermissions(irmTroop);
                    irmTroops.add(irmTroop);
                }
            }
        }catch(Exception e){
            log.error("Error Occurred:", e);
        }
        return irmTroops;
    }

    private Troop buildVTKAdminTroop(User user) {
        Troop dummyVTKAdminTroop = new Troop();
        dummyVTKAdminTroop.setSfUserId(user.getSfUserId());
        dummyVTKAdminTroop.setSfTroopId("CA_"+user.getSfUserId());
        dummyVTKAdminTroop.setTroopId("CA_"+user.getSfUserId());
        dummyVTKAdminTroop.setSfTroopName("VTK Admin View");
        dummyVTKAdminTroop.setSfCouncil(user.getAdminCouncilId());
        dummyVTKAdminTroop.setCouncilCode(user.getAdminCouncilId());
        dummyVTKAdminTroop.setRole("CA");
        dummyVTKAdminTroop.setParticipationCode("Troop");
        dummyVTKAdminTroop.setTroopName("VTK Admin View");
        dummyVTKAdminTroop.setGradeLevel(user.getAdminCouncilId());
        dummyVTKAdminTroop.setIsTransient(true);
        String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + dummyVTKAdminTroop.getSfCouncil();
        dummyVTKAdminTroop.setCouncilPath(councilPath);
        setTroopPath(dummyVTKAdminTroop);
        setTroopPermissions(dummyVTKAdminTroop);
        return dummyVTKAdminTroop;
    }

    private List<Troop> buildServiceUnitManagerTroops(User user) {
        List<Troop> troops = new ArrayList<Troop>();
        try {
            TroopInfoResponseEntity troopInfoResponseEntity = fileClient.getServiceUnitManagerTroops();
            if (troopInfoResponseEntity != null) {
                List<TroopWrapperEntity> entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopWrapperEntity entity : entities) {
                        Troop troop = TroopEntityToTroopMapper.map(entity.getTroop());
                        troop.setSfTroopId(sumCouncilCode + "_" + user.getSfUserId());
                        troop.setSfUserId(user.getSfUserId());
                        troop.setTroopId(sumCouncilCode + "_" + user.getSfUserId());
                        troop.setId(sumCouncilCode + "_" + user.getSfUserId());
                        troop.setParticipationCode(sumCouncilCode);
                        troop.setSfCouncil(sumCouncilCode);
                        troop.setIsSUM(true);
                        troop.setIsDemoTroop(false);
                        troop.setIsIRM(false);
                        setTroopPath(troop);
                        setTroopPermissions(troop);
                        troops.add(troop);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return troops;
    }

    private boolean isValidParticipationCode(Troop troop) {
        return troop.getParticipationCode() != null && (irmCouncilCode.equals(troop.getParticipationCode()) || sumCouncilCode.equals(troop.getParticipationCode()) || "Troop".equals(troop.getParticipationCode()));
    }

    private void validateTroops(List<Troop> mergedTroops) {
        Set<Troop> invalidTroops = new HashSet<>();
        for (Troop troop : mergedTroops) {
            if (troop.getSfTroopName() == null || troop.getRole() == null || troop.getGradeLevel() == null || troop.getCouncilCode() == null || !isValidParticipationCode(troop)) {
                log.debug("Ignoring troop "+troop.getSfTroopId()+ ". Check all required parameters.");
                log.debug(troop.toString());
                invalidTroops.add(troop);
            }
        }
        mergedTroops.removeAll(invalidTroops);
    }

    private void setTroopPath(Troop troop) {
        String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + troop.getSfCouncil();
        troop.setCouncilPath(councilPath);
        String troopPath = councilPath + "/troops/" + troop.getSfTroopId();
        troop.setPath(troopPath);
    }

    private void setTroopPermissions(Troop troop) {
        String roleType = troop.getRole();
        troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
        if ("PA".equals(roleType)) {
            troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
        }else {
            if ("DP".equals(roleType) || (troop.getParticipationCode() != null && troop.getParticipationCode().equals(irmCouncilCode)) || troop.getCouncilCode().equals(sumCouncilCode)) {
                troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
            }else {
                if ("CA".equals(roleType)) {
                    troop.getPermissionTokens().addAll(Permission.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
                }
            }
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

    @ObjectClassDefinition(name = "Girl Scouts VTK SalesForce Service configuration", description = "Girl Scouts VTK SalesForce Service configuration")
    public @interface Config {
        @AttributeDefinition(name = "Load json from file", description = "Force VTK to load json from file in repository", type = AttributeType.BOOLEAN) boolean isLoadFromFile();

        @AttributeDefinition(name = "Demo Council Code", description = "Three digit code to be used for demo council", type = AttributeType.STRING) String demoCouncilCode();

        @AttributeDefinition(name = "Service Unit Manager Council Code", description = "Three digit code to be used for dummy service unit manager council", type = AttributeType.STRING) String sumCouncilCode();

        @AttributeDefinition(name = "Independent Registered Girl Council Code", description = "Three digit code to be used for dummy independent registered girl council", type = AttributeType.STRING) String irmCouncilCode();

    }
}