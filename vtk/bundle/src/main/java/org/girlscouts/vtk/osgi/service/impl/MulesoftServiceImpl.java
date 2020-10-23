package org.girlscouts.vtk.osgi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.mapper.mulesoft.*;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.PrimaryGuardian;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.cache.MulesoftContactsResponseCache;
import org.girlscouts.vtk.osgi.cache.MulesoftTroopsResponseCache;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.service.*;
import org.girlscouts.vtk.rest.entity.mulesoft.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
public class MulesoftServiceImpl implements MulesoftService {
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

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    private String sumCouncilCode;
    private String irmCouncilCode;
    private Set<String> demoRoles;

    @Activate
    private void activate(Config config) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        this.sumCouncilCode = config.sumCouncilCode();
        this.irmCouncilCode = config.irmCouncilCode();
        demoRoles = new HashSet<>();
        if(config.demoRoles() != null){
            for(String role:config.demoRoles()){
                log.debug("Adding demo role: {}",role);
                demoRoles.add(role);
            }
        }
        log.info("Girl Scouts VTK SalesForce Service Activated.");
    }

    @Override
    public ApiConfig getApiConfig(org.apache.jackrabbit.api.security.user.User user) {
        log.debug("Getting apiConfig");
        ApiConfig apiConfig = new ApiConfig();
        try {
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
                UserInfoResponseEntity userInfoResponseEntity = null;
                log.debug("getting user details from mulesoft");
                userInfoResponseEntity = restClient.getUser(gsGlobalId);
                log.debug("User Entity:"+userInfoResponseEntity);
                vtkUser = UserInfoResponseEntityToUserMapper.map(userInfoResponseEntity);
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
            if(troopsCache.contains(user.getSfUserId())){
                log.debug("Getting troop details from cache");
                troopInfoResponseEntity = troopsCache.read(user.getSfUserId());
            }else{
                log.debug("Getting troop details from mulesoft");
                troopInfoResponseEntity = restClient.getTroops(user.getSfUserId());
                troopsCache.write(user.getSfUserId(), troopInfoResponseEntity);
            }
            log.debug("Troop Info Entity:"+troopInfoResponseEntity);
            if (troopInfoResponseEntity != null) {
                List<TroopWrapperEntity> entities = troopInfoResponseEntity.getTroops();
                if (entities != null) {
                    for (TroopWrapperEntity entity : entities) {
                        try {
                            Troop troop = TroopEntityToTroopMapper.map(entity.getTroop());
                            troop.setParticipationCode("Troop");
                            troop.setSfUserId(user.getSfUserId());
                            setTroopPath(troop);
                            log.debug("Adding troop: " + troop);
                            if(!StringUtils.isBlank(troop.getRole()) && ("DP".equals(troop.getRole()) || "FA".equals(troop.getRole()))){
                                troops.add(troop);
                            }
                            if(isDemoViewRequired(entity)){
                                //Comment line below if we create demo troop selection per user primary council
                                user.setServiceUnitManager(true);
                                //Uncomment line below if we create demo troop selection per troop job code
                                //troops.addAll(buildServiceUnitManagerTroops(troop));
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

    private boolean isDemoViewRequired(TroopWrapperEntity entity) {
        try{
            List<VolunteerJobsEntity> jobs = entity.getTroop().getVolunteerJobs();
            if (jobs != null) {
                for (VolunteerJobsEntity jobEntity : jobs) {
                    try {
                        String endDate = jobEntity.getEndDate();
                        String startDate = jobEntity.getStartDate();
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                        DateTime start = formatter.parseDateTime(startDate);
                        DateTime end = formatter.parseDateTime(endDate);
                        if (start.isBeforeNow() && end.isAfterNow()){
                            if(jobEntity.getJobCode() != null && demoRoles.contains(jobEntity.getJobCode())){
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred checking jobEntity {} ",jobEntity, e);
                    }
                }
            }
        }catch(Exception e){
            log.error("Exception occurred: ",e);
        }
        return false;
    }

    @Override
    public List<Contact> getContactsForTroop(Troop troop, User user) {
        log.debug("Getting contacts for troop "+troop);
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopMembersResponseEntity troopMembersResponseEntity = null;
            if (troop.getIsSUM()) {
                return getServiceUnitManagerContacts(troop, user);
            } else {
                if(contactsCache.contains(troop.getSfTroopId())){
                    troopMembersResponseEntity =  contactsCache.read(troop.getSfTroopId());
                }else{
                    if(troop.getIsIRM()) {
                        troopMembersResponseEntity = restClient.getMembers(troop.getIrmTroopId());
                    }else{
                        troopMembersResponseEntity = restClient.getMembers(troop.getSfTroopId());
                    }
                    contactsCache.write(troop.getSfTroopId(), troopMembersResponseEntity);
                }
            }
            log.debug("Troop Contacts Entity:"+troopMembersResponseEntity);
            if (troopMembersResponseEntity != null) {
                List<MembersEntity> entities = troopMembersResponseEntity.getMembers();
                if (entities != null) {
                    for (MembersEntity entity : entities) {
                        Contact contact = TroopMemberEntityToContactMapper.map(entity, troopMembersResponseEntity.getTroop());
                        if (!troop.getIsIRM()){
                            //Not IRM
                            log.debug("found non IRM contact : "+contact.getId());
                            contacts.add(contact);
                        }else{
                            //Fetching specific girl info for her irm troop
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

    private List<Contact> getServiceUnitManagerContacts(Troop troop, User user) {
        List<Contact> contacts = new ArrayList<>();
        Contact girl = new Contact();
        girl.setFirstName("Demo");
        girl.setLastName("Girl");
        girl.setType(0);
        girl.setId("demo-girl-"+user.getSfUserId());
        girl.setContactId("demo-girl-for-"+user.getSfUserId());
        girl.setEmail("demo-girl+_1@gsfuture.org");
        girl.setPhone("5155551234");
        girl.setAddress("4 Smith Dr");
        girl.setCity("Stanley");
        girl.setState("NC");
        girl.setCountry("USA");
        girl.setZip("02910");
        girl.setAge(10);
        girl.setDob("12/23/2009");
        girl.setMembershipYear_adult(0);
        girl.setMembershipYear_girl(5);
        girl.setRole("Girl");
        girl.setAccountId(user.getSfUserId());
        girl.setEmailOptIn(false);
        girl.setTxtOptIn(false);
        girl.setRenewalDue(false);
        girl.setMembershipYear(VtkUtil.getCurrentGSYear());
        PrimaryGuardian primaryGuardian = new PrimaryGuardian();
        primaryGuardian.setGlobalId(user.getSfUserId());
        primaryGuardian.setEmail(user.getEmail());
        primaryGuardian.setEmailOptIn(false);
        primaryGuardian.setFirstName("Demo");
        primaryGuardian.setGlobalId(user.getSfUserId());
        primaryGuardian.setLastName("Parent");
        primaryGuardian.setMobilePhone("5155551234");
        primaryGuardian.setPhone("5155551234");
        primaryGuardian.setSmsOptIn(false);
        girl.setPrimaryGuardian(primaryGuardian);
        Contact subContact = new Contact();
        subContact.setType(1);
        subContact.setEmail(primaryGuardian.getEmail());
        subContact.setFirstName(primaryGuardian.getFirstName());
        subContact.setLastName(primaryGuardian.getLastName());
        subContact.setId(primaryGuardian.getGlobalId());
        subContact.setContactId(primaryGuardian.getGlobalId());
        List<Contact> subContacts = new ArrayList<Contact>();
        subContacts.add(subContact);
        girl.setContacts(subContacts);
        contacts.add(girl);
        Contact adult = new Contact();
        adult.setFirstName("Demo");
        adult.setLastName("Volunteer");
        adult.setType(0);
        adult.setId("demo-volunteer-"+user.getSfUserId());
        adult.setContactId("demo-volunteer-for-"+user.getSfUserId());
        adult.setEmail("demo-volunteer+_1@gsfuture.org");
        adult.setPhone("5155551234");
        adult.setAddress("4 Smith Dr");
        adult.setCity("Stanley");
        adult.setState("NC");
        adult.setCountry("USA");
        adult.setZip("02910");
        adult.setAge(30);
        adult.setDob("12/23/2009");
        adult.setMembershipYear_adult(5);
        adult.setMembershipYear_girl(10);
        adult.setRole("Volunteer");
        adult.setAccountId(user.getSfUserId());
        adult.setEmailOptIn(false);
        adult.setTxtOptIn(false);
        adult.setRenewalDue(false);
        adult.setMembershipYear(VtkUtil.getCurrentGSYear());
        adult.setPrimaryGuardian(primaryGuardian);
        adult.setContacts(subContacts);
        contacts.add(adult);
        return contacts;
    }

    @Override
    public List<Contact> getTroopLeaders(Troop troop) {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            TroopLeadersResponseEntity troopLeadersInfoResponseEntity = null;
            troopLeadersInfoResponseEntity = restClient.getTroopLeaders(troop.getSfTroopId());
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
            if(contactsCache.contains(accountId)){
                troopMembersResponseEntity =  contactsCache.read(accountId);
            }else{
                troopMembersResponseEntity = restClient.getMembers(accountId);
                contactsCache.write(accountId, troopMembersResponseEntity);
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
        if(contact != null && contact.getRole() != null && "Girl".equals(contact.getRole()) && contact.getPrimaryGuardian() != null && contact.getPrimaryGuardian().getGlobalId() != null){
            String userGlobalId = user.getSfUserId();
            String primaryGuardianGlobalId = contact.getPrimaryGuardian().getGlobalId();
            if(userGlobalId.equals(primaryGuardianGlobalId)){
                result = true;
            }
        }
        return result;
    }


    private void setTroopsForUser(User user, UserInfoResponseEntity userInfoResponseEntity) {
        List<Troop> parentTroops = getParentTroops(user, userInfoResponseEntity);
        List<Troop> additionalTroops = getTroops(user);
        List<Troop> mergedTroops = mergeParentAndJobTroops(parentTroops, additionalTroops);
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
            String role = troop.getRole();
            switch(role){
                case "PA":
                    troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
                    break;
                case "DP":
                    troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
                    break;
                case "FA":
                    troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_FINANCE_PERMISSIONS));
                    break;
                case "CA":
                    troop.setPermissionTokens(Permission.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
                    break;
            }
            troop.setHash(troopHashGenerator.hash(troop));
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
                List<String> processedIRMAccounts = new ArrayList<>();
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
                                            if(!processedIRMAccounts.contains(troop.getSfTroopId())) {
                                                parentTroops.addAll(getIRMTroops(user, troop.getSfTroopId(), troop.getCouncilCode()));
                                                processedIRMAccounts.add(troop.getSfTroopId());
                                            }
                                        } else {
                                            log.debug("Parent Affiliation:" + entity);
                                            setTroopPath(troop);
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
    private Troop buildTroop(String userId, String troopId, String councilCode, String role, String participationCode, String troopName, String gradeLevel, boolean isSUM, boolean isIRM, boolean isTransient){
        Troop troop = new Troop();
        troop.setSfUserId(userId);
        troop.setSfTroopId(troopId);
        troop.setTroopId(troopId);
        troop.setId(troopId);
        troop.setSfCouncil(councilCode);
        troop.setCouncilCode(councilCode);
        troop.setCouncilId(councilCode);
        troop.setRole(role);
        troop.setParticipationCode(participationCode);
        troop.setTroopName(troopName);
        troop.setSfTroopName(troopName);
        troop.setGradeLevel(gradeLevel);
        troop.setSfTroopAge(gradeLevel);
        troop.setIsSUM(isSUM);
        troop.setIsIRM(isIRM);
        troop.setIsTransient(isTransient);
        setTroopPath(troop);
        return troop;
    }
    private List<Troop> getIRMTroops(User user, String accountId, String councilCode) {
        log.debug("Generating IRM troops for {}", user.getSfUserId());
        List<Troop> irmTroops = new ArrayList<Troop>();
        try {
            //Generating dummy troops for each IRM girl under user
            List<Contact> contacts = getContactsForIRMParent(accountId, user);
            for (Contact contact : contacts) {
                if (contact != null && "Girl".equals(contact.getRole()) && isChildOfUser(contact, user)) {
                    String troopName = "";
                    if (contact.getFirstName() != null) {
                        troopName = contact.getFirstName()+" ";
                    }
                    if (contact.getLastName() != null) {
                        troopName += contact.getLastName();
                    }
                    Troop irmTroop = buildTroop(
                            user.getSfUserId(),
                            irmCouncilCode + "_" + contact.getId(),
                            councilCode,
                            "DP",
                            irmCouncilCode,
                            troopName,
                            "7-Multi-level",
                            false,
                            true,
                            false);
                    irmTroop.setIrmTroopId(accountId);// this is needed to retrieve contacts later
                    log.debug("adding IRM troop " + irmTroop);
                    irmTroops.add(irmTroop);
                }
            }
        }catch(Exception e){
            log.error("Error Occurred:", e);
        }
        log.debug("Generated IRM troops: {}", irmTroops);
        return irmTroops;
    }

    private Troop buildVTKAdminTroop(User user) {
        log.debug("Building VTK Admin troop for: {}", user.getSfUserId());
        Troop dummyVTKAdminTroop = buildTroop(
                user.getSfUserId(),
                "CA_"+user.getSfUserId(),
                user.getAdminCouncilId(),
                "CA",
                "Troop",
                "VTK Admin View",
                user.getAdminCouncilId(),
                false,
                false,
                true);
        log.debug("Generated VTK Admin troop: ", dummyVTKAdminTroop);
        return dummyVTKAdminTroop;
    }

    private List<Troop> buildServiceUnitManagerTroops(Troop troop) {
        List<Troop> troops = new ArrayList<Troop>();
        Troop troopLeaderTroop = buildTroop(
                troop.getSfUserId(),
                sumCouncilCode + "_" + troop.getSfUserId(),
                troop.getCouncilCode(),
                "DP",
                "Troop",
                troop.getCouncilCode()+ " Demo Troop Leader",
                "7-Multi-level",
                true,
                false,
                false);
        troops.add(troopLeaderTroop);
        Troop parentTroop = buildTroop(
                troop.getSfUserId(),
                sumCouncilCode + "_" + troop.getSfUserId(),
                troop.getCouncilCode(),
                "PA",
                "Troop",
                troop.getCouncilCode()+ " Demo Parent",
                "7-Multi-level",
                true,
                false,
                false);
        troops.add(parentTroop);
        return troops;
    }
    private List<Troop> buildServiceUnitManagerTroops(User user) {
        log.debug("Building demo troops for: {}", user.getSfUserId());
        List<Troop> troops = new ArrayList<Troop>();
        Troop troopLeaderTroop = buildTroop(
                user.getSfUserId(),
                sumCouncilCode + "_" + user.getSfUserId(),
                user.getAdminCouncilId(),
                "DP",
                "Troop",
                "Demo Troop Leader",
                "7-Multi-level",
                true,
                false,
                false);
        troops.add(troopLeaderTroop);
        Troop parentTroop = buildTroop(
                user.getSfUserId(),
                sumCouncilCode + "_" + user.getSfUserId(),
                user.getAdminCouncilId(),
                "PA",
                "Troop",
                "Demo Parent",
                "7-Multi-level",
                true,
                false,
                false);
        troops.add(parentTroop);
        log.debug("Generated demo troops: {}", troops);
        return troops;
    }

    private boolean isValidParticipationCode(Troop troop) {
        return troop.getParticipationCode() != null && (irmCouncilCode.equals(troop.getParticipationCode()) || "Troop".equals(troop.getParticipationCode()));
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

    private List<Troop> mergeParentAndJobTroops(List<Troop> parentTroops, List<Troop> jobTroops) {
        List<Troop> resultTroops = new ArrayList<>();
        if ((parentTroops == null && jobTroops == null) || (parentTroops.size() <= 0 && jobTroops.size() <= 0)) {
            return new ArrayList();
        }
        if ((parentTroops == null || parentTroops.size() <= 0) && (jobTroops != null)) {
            return jobTroops;
        }
        if ((jobTroops == null || jobTroops.size() <= 0) && parentTroops != null) {
            return parentTroops;
        }
        resultTroops.addAll(parentTroops);
        resultTroops.addAll(jobTroops);
        resultTroops = mergeRoles(resultTroops);
        return resultTroops;
    }

    private List<Troop> mergeRoles(List<Troop> jobTroops) {
        Map<String,Troop> troopMap = new HashMap<>();
        for (int i = 0; i < jobTroops.size(); i++) {
            Troop troop1 = jobTroops.get(i);
            Set<String> roles = new HashSet<>();
            roles.add(troop1.getRole());
            if(!troopMap.containsKey(troop1.getTroopId())){
                for (int j = i; j < jobTroops.size(); j++) {
                    Troop troop2 = jobTroops.get(j);
                    if(troop1.getTroopId().equals(troop2.getTroopId())){
                        roles.add(troop2.getRole());
                    }
                }
            }
            String role = "";
            if (roles.contains("PA")) {
                role = "PA";
            }
            if (roles.contains("FA")) {
                role = "FA";
            }
            if (roles.contains("DP")) {
                role = "DP";
            }
            if(roles.contains("CA")) {
                role = "CA";
            }
            troop1.setRole(role);
            log.debug("Troop: {}, Role: {}", troop1.getTroopId(),troop1.getRole());
            troopMap.put(troop1.getTroopId(),troop1);
        }
        jobTroops = new ArrayList<>();
        jobTroops.addAll(troopMap.values());
        return jobTroops;
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

        @AttributeDefinition(name = "Service Unit Manager Council Code", description = "Three digit code to be used for dummy service unit manager council", type = AttributeType.STRING) String sumCouncilCode() default "SUM";

        @AttributeDefinition(name = "Independent Registered Girl Council Code", description = "Three digit code to be used for dummy independent registered girl council", type = AttributeType.STRING) String irmCouncilCode() default "IRM";

        @AttributeDefinition(name = "Demo Roles") String[] demoRoles() default {
                "Coach",
                "Manager",
                "Mentor",
                "Product Program - Fall Product",
                "Product Program - Cookie Program",
                "Recruiter/Liaison",
                "Supporter",
                "Trainer",
                "Volunteer"
        };
    }
}