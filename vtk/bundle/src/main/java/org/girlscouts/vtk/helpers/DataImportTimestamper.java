package org.girlscouts.vtk.helpers;

import java.util.Date;

public interface DataImportTimestamper {
	Date getTimestamp();

	void setTimestamp();

	void setTimestamp(Date date);
}
