package org.girlscouts.vtk.testing.tools;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.girlscouts.vtk.replication.VTKDataCacheInvalidator;

public class VtkJob implements Job {

	
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		VTKDataCacheInvalidator invalidator = null;
		try {
			invalidator = (VTKDataCacheInvalidator) ctx.getScheduler().getContext().get("invalidator");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		invalidator.execute(null);

	}

}
