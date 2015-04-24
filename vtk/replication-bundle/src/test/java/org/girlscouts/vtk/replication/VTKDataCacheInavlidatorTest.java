package org.girlscouts.vtk.replication;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.vtk.testing.tools.VtkJob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClient.class, GetMethod.class})
public class VTKDataCacheInavlidatorTest {

	VTKDataCacheInvalidator invalidator;
	
	
	Set<String> paths;
	
	
	@Mock
	GetMethod get;
	
	@Mock
	HttpClient httpClient;
	
	
	@Mock
	org.apache.sling.commons.scheduler.Scheduler scheduler;
	
	Scheduler mockScheduler;
	
	@Mock
	SlingRepository repository;
	
	@Mock
	Session session;
	
	@Mock
	Node node;
	
	@Mock
	Property property;
	
	int interval;
	String jobName;
	String flushNode;
	String flushProperty;
	
	String flushURI = "something";
	
	Job vtkJob;
	
	org.apache.sling.commons.scheduler.Scheduler delegatorScheduler;
	
	
	
	
	@Before
	public void init(){
		try {
			//get the private static final fields
			
			Field intervalField = VTKDataCacheInvalidator.class.getDeclaredField("INTERVAL");
			intervalField.setAccessible(true);
			interval = intervalField.getInt(null);
			
			Field jobNameField = VTKDataCacheInvalidator.class.getDeclaredField("JOB_NAME");
			jobNameField.setAccessible(true);
			jobName = (String) jobNameField.get(null);
			
			Field flushNodeField = VTKDataCacheInvalidator.class.getDeclaredField("FLUSH_NODE");
			flushNodeField.setAccessible(true);
			flushNode = (String) flushNodeField.get(null);
			
			Field flushPropertyField = VTKDataCacheInvalidator.class.getDeclaredField("FLUSH_PROPERTY");
			flushPropertyField.setAccessible(true);
			flushProperty = (String) flushPropertyField.get(null);
			
			
			invalidator = new VTKDataCacheInvalidator();
			
			mockScheduler = StdSchedulerFactory.getDefaultScheduler();
			
			
			//Set fields injected by the framework during normal execution
			Field repositoryField = invalidator.getClass().getDeclaredField("repository");
			repositoryField.setAccessible(true);
			repositoryField.set(invalidator, repository);
			
			Field schedulerField = invalidator.getClass().getDeclaredField("scheduler");
			schedulerField.setAccessible(true);
			schedulerField.set(invalidator, scheduler);
		
			
			
			
			mockScheduler.getContext().put("invalidator", invalidator);
			
			
			
			delegatorScheduler = new org.apache.sling.commons.scheduler.Scheduler(){

				//Useless implements because Easy Mock is inflexible in certain regards
				public void addJob(String arg0, Object arg1, Map<String, Serializable> arg2, String arg3, boolean arg4) throws Exception {}
				public void addPeriodicJob(String arg0, Object arg1, Map<String, Serializable> arg2, long arg3, boolean arg4) throws Exception {}
				public void addPeriodicJob(String arg0, Object arg1, Map<String, Serializable> arg2, long arg3, boolean arg4, boolean arg5) throws Exception {}
				public void fireJob(Object arg0, Map<String, Serializable> arg1) throws Exception {}
				public boolean fireJob(Object arg0, Map<String, Serializable> arg1, int arg2, long arg3) {return false;}
				//Only method we need
				public void fireJobAt(String arg0, Object arg1,
						Map<String, Serializable> arg2, Date arg3)
						throws Exception {
					//The only part we need
					mockScheduler.scheduleJob(newJob(VtkJob.class).build() , newTrigger().startAt(new Date(new Date().getTime() + interval)).build());
					
				}
				public boolean fireJobAt(String arg0, Object arg1, Map<String, Serializable> arg2, Date arg3, int arg4, long arg5) {return false;}
				public void removeJob(String arg0) throws NoSuchElementException {}
			};
			
			
			
			
		} catch (NoSuchFieldException e) {
			fail("NoSuch FieldException Thrown");
		} catch (SecurityException e) {
			fail("SecurityException Thrown");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException Thrown");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException Thrown");
		} catch (SchedulerException e) {
			fail("Unable to intiate mock scheduler");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSuccessfulScheduleStatusOtherSingleCall() {
		
		try {
			mockScheduler.start();
			
			//Setup basic initial expected behavior on relevant mocks
			
			expectNew(HttpClient.class).andReturn(httpClient);
			
			expect(repository.loginAdministrative(anyObject(String.class))).andReturn(session);
			expect(session.getNode(flushNode)).andReturn(node);
			expect(node.getProperty(flushProperty)).andReturn(property);
			expect(property.getString()).andReturn(flushURI);
			session.logout();
			expectLastCall();
			
			
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));	
			expectLastCall().andDelegateTo(delegatorScheduler);
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader(anyObject(String.class), anyObject(String.class));
			expectLastCall().times(3);
			
			expect(httpClient.executeMethod(get)).andReturn(404);
			
			replayAll();
			
			invalidator.init();
			invalidator.addPath("sample path");
			Thread.sleep(2000);
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			Set<String> paths = (Set<String>) pathsField.get(invalidator);
			
			assertTrue(paths.isEmpty());
			
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

