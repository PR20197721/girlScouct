package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.rest.entity.mulesoft.DPInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DPInfoEntityToContactMapper {
    private static Logger log = LoggerFactory.getLogger(DPInfoEntity.class);

    public static Contact map(DPInfoEntity entity) {
        try {
            Contact contact = new Contact();
            contact.setType(0);
            try {
                contact.setFirstName(entity.getFirstName());
            } catch (Exception ex) {
                log.error("Error occurred mapping FirstName to Contact ", ex);
            }
            try {
                contact.setLastName(entity.getLastName());
            } catch (Exception ex) {
                log.error("Error occurred mapping LastName to Contact ", ex);
            }
            return contact;
        } catch (Exception e) {
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
