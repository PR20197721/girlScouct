package org.girlscouts.vtk.mapper.mulesoft;

import org.apache.commons.lang.StringUtils;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.PrimaryGuardian;
import org.girlscouts.vtk.rest.entity.mulesoft.MembersEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TroopMemberEntityToContactMapper {
    private static Logger log = LoggerFactory.getLogger(TroopMemberEntityToContactMapper.class);

    public static Contact map(MembersEntity entity, TroopEntity troop) {
        try {
            Contact contact = new Contact();
            contact.setType(0);
            try {
                contact.setId(entity.getGlobalId());
                contact.setContactId(entity.getGlobalId());
            } catch (Exception ex) {
                log.error("Error occurred mapping sfId to Contact ", ex);
            }
            try {
                contact.setFirstName(entity.getFirstName());
            } catch (Exception ex) {
                log.error("Error occurred mapping First Name to Contact ", ex);
            }
            try {
                contact.setLastName(entity.getLastName());
            } catch (Exception ex) {
                log.error("Error occurred mapping Last Name to Contact ", ex);
            }
            try {
                contact.setEmail(entity.getEmail());
            } catch (Exception ex) {
                log.error("Error occurred mapping Email to Contact ", ex);
            }
            try {
                contact.setPhone(entity.getPhone());
            } catch (Exception ex) {
                log.error("Error occurred mapping Phone to Contact ", ex);
            }
            if(entity.getAddress() != null) {
                try {
                    contact.setAddress(entity.getAddress().getStreet());
                } catch (Exception ex) {
                    log.error("Error occurred mapping MailingStreet to Contact ", ex);
                }
                try {
                    contact.setCity(entity.getAddress().getCity());
                } catch (Exception ex) {
                    log.error("Error occurred mapping MailingCity to Contact ", ex);
                }
                try {
                    contact.setState(entity.getAddress().getState());
                } catch (Exception ex) {
                    log.error("Error occurred mapping MailingStateto Contact ", ex);
                }
                try {
                    contact.setCountry(entity.getAddress().getCountry());
                } catch (Exception ex) {
                    log.error("Error occurred mapping MailingCountry to Contact ", ex);
                }
                try {
                    contact.setZip(entity.getAddress().getPostalCode());
                } catch (Exception ex) {
                    log.error("Error occurred mapping MailingPostalCode to Contact ", ex);
                }
            }
            try {
                contact.setAge(entity.getAge());
            } catch (Exception ex) {
                log.error("Error occurred mapping Age to Contact ", ex);
            }
            try {
                contact.setDob(entity.getBirthdate());
            } catch (Exception ex) {
                log.error("Error occurred mapping Birthdate to Contact ", ex);
            }
            try {
                contact.setMembershipYear_adult(entity.getYearsAsAdult());
            } catch (Exception ex) {
                log.error("Error occurred mapping AdultYears to Contact ", ex);
            }
            try {
                contact.setMembershipYear_girl(entity.getYearsAsGirl());
            } catch (Exception ex) {
                log.error("Error occurred mapping GirlYears to Contact ", ex);
            }
            try {
                String role = entity.getRole().getPrimary();
                if(role == null){
                    role = entity.getRole().getSecondary();
                }
                if(role != null && "Volunteer".equals(role)){
                    role = "Adult";
                }
                contact.setRole(role);
            } catch (Exception ex) {
                log.error("Error occurred mapping Role to Contact ", ex);
            }
            try {
                contact.setAccountId(troop.getId());
            } catch (Exception ex) {
                log.error("Error occurred mapping SfAccountId to Contact ", ex);
            }
            try {
                contact.setEmailOptIn(entity.isEmailOptIn());
            } catch (Exception ex) {
                log.error("Error occurred mapping EmailOptIn to Contact ", ex);
            }
            try {
                contact.setTxtOptIn(entity.isSmsOptIn());
            } catch (Exception ex) {
                log.error("Error occurred mapping TextPhoneOptIn to Contact ", ex);
            }
            try {
                Boolean isRenewal = entity.isPromptForRenewal();
                contact.setRenewalDue(isRenewal == null ? false : isRenewal);
            } catch (Exception ex) {
                log.error("Error occurred mapping isRenewal to Contact ", ex);
            }
            try {
                if(!StringUtils.isBlank(entity.getMostRecentMembershipYear())) {
                    Integer membershipYear = Integer.parseInt(entity.getMostRecentMembershipYear());
                    contact.setMembershipYear(membershipYear);
                }
            } catch (Exception ex) {
                log.error("Error occurred mapping membershipYear to Contact ", ex);
            }
            PrimaryGuardian primaryGuardian = new PrimaryGuardian();
            if(entity.getPrimaryGuardian() != null) {
                primaryGuardian.setGlobalId(entity.getPrimaryGuardian().getGlobalId());
                primaryGuardian.setEmail(entity.getPrimaryGuardian().getEmail());
                primaryGuardian.setEmailOptIn(entity.getPrimaryGuardian().isEmailOptIn());
                primaryGuardian.setFirstName(entity.getPrimaryGuardian().getFirstName());
                primaryGuardian.setGlobalId(entity.getPrimaryGuardian().getGlobalId());
                primaryGuardian.setLastName(entity.getPrimaryGuardian().getLastName());
                primaryGuardian.setMobilePhone(entity.getPrimaryGuardian().getMobilePhone());
                primaryGuardian.setPhone(entity.getPrimaryGuardian().getPhone());
                primaryGuardian.setSmsOptIn(entity.getPrimaryGuardian().isSmsOptIn());
            }
            contact.setPrimaryGuardian(primaryGuardian);
            try {
                Contact subContact = new Contact();
                subContact.setType(1);
                try {
                    subContact.setEmail(primaryGuardian.getEmail());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account Email to Sub Contact ", ex);
                }
                try {
                    subContact.setFirstName(primaryGuardian.getFirstName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account FirstName to Sub Contact ", ex);
                }
                try {
                    subContact.setLastName(primaryGuardian.getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account LastName to Sub Contact ", ex);
                }
                try {
                    subContact.setId(primaryGuardian.getGlobalId());
                    subContact.setContactId(primaryGuardian.getGlobalId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping sub contact Global Id to Sub Contact id", ex);
                }
                List<Contact> subContacts = new ArrayList<Contact>();
                subContacts.add(subContact);
                contact.setContacts(subContacts);
            } catch (Exception ex2) {
                log.error("Error occurred mapping Account to Contact ", ex2);
            }
            log.debug("Mapped contact: "+contact);
            return contact;
        } catch (Exception e) {
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
