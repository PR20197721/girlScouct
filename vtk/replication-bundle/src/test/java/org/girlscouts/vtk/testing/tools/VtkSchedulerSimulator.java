package org.girlscouts.vtk.testing.tools;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class VtkSchedulerSimulator {
	
	Scheduler scheduler;
	
	
	public boolean init(){
		try {
			this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean start(){
		try {
			this.scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean shutdown(){
		try {
			this.scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean schedule(Job job, long time, boolean once){
		
		
		return true;
	}
}
