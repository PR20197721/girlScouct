package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.salesforce.ContactEntity;
import org.girlscouts.vtk.rest.entity.salesforce.ParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContactEntityToContactMapper {
    private static Logger log = LoggerFactory.getLogger(ContactEntityToContactMapper.class);

    public Contact map(ContactEntity entity){
        try {
            Contact contact = new Contact();
            contact.setType(0);
            try {
                contact.setId(entity.getSfId());
            }catch(Exception ex){
                log.error("Error occurred mapping sfId to Contact ", ex);
            }
            try {
                contact.setFirstName(entity.getName());
            }catch(Exception ex){
                log.error("Error occurred mapping Name to Contact ", ex);
            }
            try {
                contact.setEmail(entity.getEmail());
            }catch(Exception ex){
                log.error("Error occurred mapping Email to Contact ", ex);
            }
            try {
                contact.setPhone(entity.getPhone());
            }catch(Exception ex){
                log.error("Error occurred mapping Phone to Contact ", ex);
            }
            try {
                contact.setAddress(entity.getMailingStreet());
            }catch(Exception ex){
                log.error("Error occurred mapping MailingStreet to Contact ", ex);
            }
            try {
                contact.setCity(entity.getMailingCity());
            }catch(Exception ex){
                log.error("Error occurred mapping MailingCity to Contact ", ex);
            }
            try {
                contact.setState(entity.getMailingState());
            }catch(Exception ex){
                log.error("Error occurred mapping MailingStateto Contact ", ex);
            }
            try {
                contact.setCountry(entity.getMailingCountry());
            }catch(Exception ex){
                log.error("Error occurred mapping MailingCountry to Contact ", ex);
            }
            try {
                contact.setZip(entity.getMailingPostalCode());
            }catch(Exception ex){
                log.error("Error occurred mapping MailingPostalCode to Contact ", ex);
            }
            try {
                contact.setAge(entity.getAge());
            }catch(Exception ex){
                log.error("Error occurred mapping Age to Contact ", ex);
            }
            try {
                contact.setDob(entity.getBirthdate());
            }catch(Exception ex){
                log.error("Error occurred mapping Birthdate to Contact ", ex);
            }
            try {
                contact.setMembershipYear_adult(entity.getAdultYears());
            }catch(Exception ex){
                log.error("Error occurred mapping AdultYears to Contact ", ex);
            }
            try {
                contact.setMembershipYear_girl(entity.getGirlYears());
            }catch(Exception ex){
                log.error("Error occurred mapping GirlYears to Contact ", ex);
            }
            try {
                contact.setRole(entity.getRole());
            }catch(Exception ex){
                log.error("Error occurred mapping Role to Contact ", ex);
            }
            try {
                contact.setAccountId(entity.getSfAccountId());
            }catch(Exception ex){
                log.error("Error occurred mapping SfAccountId to Contact ", ex);
            }
            try {
                contact.setEmailOptIn(entity.isEmailOptIn());
            } catch (Exception ex) {
                log.error("Error occurred mapping EmailOptIn to Contact ", ex);
            }
            try {
                contact.setTxtOptIn(entity.isTextPhoneOptIn());
            } catch (Exception ex) {
                log.error("Error occurred mapping TextPhoneOptIn to Contact ", ex);
            }
            try {
                contact.setTxtOptIn(entity.isTextPhoneOptIn());
            } catch (Exception ex) {
                log.error("Error occurred mapping TextPhoneOptIn to Contact ", ex);
            }
            try {
                Contact subContact = new Contact();
                subContact.setType(1);
                try {
                    subContact.setEmail(entity.getAccount().getPreferredContact().getEmail());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account Email to Sub Contact ", ex);
                }
                try {
                    subContact.setFirstName(entity.getAccount().getPreferredContact().getFirstName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account FirstName to Sub Contact ", ex);
                }
                try {
                    subContact.setLastName(entity.getAccount().getPreferredContact().getLastName());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account LastName to Sub Contact ", ex);
                }
                try {
                    subContact.setContactId(entity.getAccount().getPreferredContact().getSfId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account sfId to Sub Contact ", ex);
                }
                try {
                    subContact.setContactId(entity.getAccount().getPreferredContact().getSfId());
                } catch (Exception ex) {
                    log.error("Error occurred mapping Account sfId to Sub Contact ", ex);
                }
                List<Contact> subContacts = new ArrayList<Contact>();
                subContacts.add(subContact);
                contact.setContacts(subContacts);
            } catch (Exception ex2) {
                log.error("Error occurred mapping Account to Contact ", ex2);
            }
            return contact;
        }catch(Exception e){
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
