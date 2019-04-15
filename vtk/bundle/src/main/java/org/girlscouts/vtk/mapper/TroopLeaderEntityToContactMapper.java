package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.rest.entity.salesforce.TroopLeaderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroopLeaderEntityToContactMapper {
    private static Logger log = LoggerFactory.getLogger(TroopLeaderEntityToContactMapper.class);

    public Contact map(TroopLeaderEntity entity){
        try {
            Contact contact = new Contact();
            contact.setType(0);
            try {
                contact.setId(entity.getContact().getSfId());
            }catch(Exception ex){
                log.error("Error occurred mapping sfId to Contact ", ex);
            }
            try {
                contact.setFirstName(entity.getContact().getFirstName());
            }catch(Exception ex){
                log.error("Error occurred mapping Name to Contact ", ex);
            }
            try {
                contact.setFirstName(entity.getContact().getFirstName());
            }catch(Exception ex){
                log.error("Error occurred mapping Name to Contact ", ex);
            }
            return contact;
        }catch(Exception e){
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
