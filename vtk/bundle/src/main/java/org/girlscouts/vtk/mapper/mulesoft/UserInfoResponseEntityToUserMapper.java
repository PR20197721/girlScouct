package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoResponseEntityToUserMapper {
    private static Logger log = LoggerFactory.getLogger(UserInfoResponseEntityToUserMapper.class);

    public static User map(UserInfoResponseEntity entity) {
        try {
            User user = new User();
            if (entity != null) {
                try {
                    user.setSfUserId(entity.getGlobalId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping gs global id to User ", ex);
                }
                try {
                    user.setEmail(entity.getEmail());
                } catch (Exception ex) {
                    log.error("Error occurred mapping email to User ", ex);
                }
                try {
                    user.setFirstName(entity.getFirstName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping FirstName to User ", ex);
                }
                try {
                    user.setLastName(entity.getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping LastName to User ", ex);
                }
                try {
                    user.setName(user.getFirstName() + " " + user.getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Name to User ", ex);
                }
                try {
                    user.setPhone(entity.getPhone());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Phone to User ", ex);
                }
                try {
                    user.setContactId(entity.getPrimaryContact().getGlobalId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Contact Id to User ", ex);
                }
                try {
                    String renewalDate = entity.getRenewalDate();
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
                    DateTime dt = formatter.parseDateTime(renewalDate);
                	user.setActive(dt.isAfterNow());
                } catch (Exception ex) {
                    log.error("Error occurred mapping ActiveDP to User ", ex);
                }
                try {
                    user.setAdmin(entity.getProductAccess().isVtkAdmin());
                } catch (Exception ex) {
                    log.error("Error occurred mapping admin flag to User ", ex);
                }
                try {
                    user.setAdminCouncilId(entity.getPrimaryCouncil());
                } catch (Exception ex) {
                    log.error("Error occurred mapping admin council code to User ", ex);
                }
                try {
                    user.setServiceUnitManager(entity.getProductAccess().isSu());
                } catch (Exception ex) {
                    log.error("Error occurred mapping admin council code to User ", ex);
                }
                return user;
            }
        } catch (Exception e) {
            log.error("Error occurred mapping UserInfoResponseEntity to User ", e);
        }
        return null;
    }
}
