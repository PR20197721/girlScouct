package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.ejb.EmailMeetingReminder;
import org.girlscouts.vtk.rest.entity.vtk.EmailMeetingReminderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailMeetingReminderEntityMapper extends BaseModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(TroopToTroopEntityMapper.class);

    public static EmailMeetingReminderEntity map(EmailMeetingReminder emailMeetingReminder) {
        if (emailMeetingReminder != null) {
            try {
                EmailMeetingReminderEntity entity = new EmailMeetingReminderEntity();
                entity.setBcc(emailMeetingReminder.getBcc());
                entity.setCc(emailMeetingReminder.getCc());
                entity.setEmailToGirlParent(emailMeetingReminder.getEmailToGirlParent());
                entity.setEmailToSelf(emailMeetingReminder.getEmailToSelf());
                entity.setEmailToTroopVolunteer(emailMeetingReminder.getEmailToTroopVolunteer());
                entity.setFrom(emailMeetingReminder.getFrom());
                entity.setHtml(emailMeetingReminder.getHtml());
                entity.setMeetingId(emailMeetingReminder.getMeetingId());
                entity.setSubj(emailMeetingReminder.getSubj());
                entity.setTemplate(emailMeetingReminder.getTemplate());
                entity.setTo(emailMeetingReminder.getTo());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping EmailMeetingReminder to EmailMeetingReminderEntity ", e);
            }
        }
        return null;
    }
}
