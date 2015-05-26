package org.girlscouts.vtk.testing.tools;

import org.girlscouts.vtk.replication.VTKDataCacheInvalidator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class CallPathJob implements Job {

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			int iterator = (Integer) ctx.getScheduler().getContext().get("iter");
			String pathname = "somepath/" + iterator;
			iterator++;
			ctx.getScheduler().getContext().put("iter", iterator);
			
			VTKDataCacheInvalidator invalidator = (VTKDataCacheInvalidator) ctx.getScheduler().getContext().get("invalidator");
			invalidator.addPath(pathname);
			
			
			
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
