package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.rest.entity.salesforce.UserEntity;
import org.girlscouts.vtk.rest.entity.salesforce.UserInfoResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoResponseEntityToUserMapper {

    private static Logger log = LoggerFactory.getLogger(UserInfoResponseEntityToUserMapper.class);

    public User map(UserInfoResponseEntity entity, User user){
        try {
            if (entity != null && entity.getUsers() != null && user != null) {
                UserEntity userEntity = entity.getUsers()[0];
                try {
                    user.setSfUserId(userEntity.getSfId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping salesforce id to User ", ex);
                }
                try {
                    user.setEmail(userEntity.getEmail());
                } catch (Exception ex) {
                    log.error("Error occurred mapping email to User ", ex);
                }
                try {
                    user.setFirstName(userEntity.getFirstName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping FirstName to User ", ex);
                }
                try {
                    user.setLastName(userEntity.getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping LastName to User ", ex);
                }
                try {
                    user.setName(user.getFirstName()+" "+user.getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Name to User ", ex);
                }
                try {
                    user.setPhone(userEntity.getPhone());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Phone to User ", ex);
                }
                try {
                    user.setContactId(userEntity.getSfContactId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Contact Id to User ", ex);
                }
                try {
                    user.setAdmin(userEntity.getContact().isVtkAdmin());
                } catch (Exception ex) {
                    log.error("Error occurred mapping admin flag to User ", ex);
                }
                try {
                    user.setAdminCouncilId(userEntity.getContact().getOwner().getCouncilCode());
                } catch (Exception ex) {
                    log.error("Error occurred mapping admin council code to User ", ex);
                }
            }
        }catch(Exception e){
            log.error("Error occurred mapping UserInfoResponseEntity to User ", e);
        }
        return null;
    }
}
