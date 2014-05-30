package org.girlscouts.vtk.models.api;

import java.util.Date;

/**
 * An email archive.
 * We do not save the recipients.
 * 
 * @author mike
 *
 */
public interface EmailArchive {
    /**
     * @return the type {@link EmailArchiveType}.
     */
    EmailArchiveType getType();
    
    /**
     * @return the date that this email was sent.
     */
    Date getSentDate();
    
    /**
     * @return the content of this email with HTML tags.
     */
    String getContent();
}
