package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.rest.entity.vtk.SentEmailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentEmailToSentEmailEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(SentEmailToSentEmailEntityMapper.class);

    public static SentEmailEntity map(SentEmail sentEmail){
        if(sentEmail != null) {
            try {
                SentEmailEntity entity = new SentEmailEntity();
                entity.setAddresses(sentEmail.getAddresses());
                entity.setAddressList(sentEmail.getAddressList());
                entity.setHtmlDiff(sentEmail.getHtmlDiff());
                entity.setPath(sentEmail.getPath());
                entity.setSentDate(sentEmail.getSentDate());
                entity.setSubject(sentEmail.getSubject());
                entity.setUid(sentEmail.getUid());
            } catch (Exception e) {
                log.error("Error occurred mapping SentEmail to SentEmailEntity ", e);
            }
        }
        return null;
    }
}
