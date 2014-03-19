package org.girlscouts.web.dataimport;

import org.girlscouts.web.exception.GirlScoutsException;

public interface DataImporter {
    public void importIntoJcr() throws GirlScoutsException;
}
