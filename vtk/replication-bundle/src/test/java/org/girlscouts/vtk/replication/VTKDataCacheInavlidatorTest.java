package org.girlscouts.vtk.replication;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertFalse;
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
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.vtk.testing.tools.CallPathJob;
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
//This is done in order to be able to intercept calls to constructors for the HttpClient and GetMethod classes within the VTKDataCacheInvalidator class
@PrepareForTest({HttpClient.class, GetMethod.class, VTKDataCacheInvalidator.class})
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
	public void setup(){
		try {
			//get the private static final fields because they are used in the method calls within the invalidator
			//And we need to have the values in order to properly set expectations for the methods to be called
			
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
			
			
			//Create a plain instance of the invalidator this is not a mock because we will need to actually test it
			invalidator = new VTKDataCacheInvalidator();
			
			//Create a quartz scheduler to simulate the sling scheduler
			mockScheduler = StdSchedulerFactory.getDefaultScheduler();
			
			
			//Set fields injected by the framework during normal execution
			Field repositoryField = invalidator.getClass().getDeclaredField("repository");
			repositoryField.setAccessible(true);
			repositoryField.set(invalidator, repository);
			
			Field schedulerField = invalidator.getClass().getDeclaredField("scheduler");
			schedulerField.setAccessible(true);
			schedulerField.set(invalidator, scheduler);
		
			
			
			//Give the quartz scheduler access to the invalidator so that it can call execute on it
			mockScheduler.getContext().put("invalidator", invalidator);
			mockScheduler.getContext().put("iter", 1);
			
			
			//Because the fireJobAt method returns void and we need to change the logic it performs we can only relate on the delegate to 
			//and that forces us to delegate to a class that implements the same interface we need to define the class below
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
			
			
			//Intercept the constructor of HttpClient and return a mocked httpclient
			expectNew(HttpClient.class).andReturn(httpClient);
			
			//a call on the already injected mock repository gives us a mock session
			expect(repository.loginAdministrative(null)).andReturn(session);
			//a call on the mock session gives us a mock node
			expect(session.getNode(flushNode)).andReturn(node);
			//a call on the mock node gives us a mock property
			expect(node.getProperty(flushProperty)).andReturn(property);
			//a call on the mock property gives us a predetermined flushuri string
			expect(property.getString()).andReturn(flushURI);
			
			session.logout();
			expectLastCall();
			
			
			
			
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
			fail("Generic exception message is: " + e.getMessage());
		} 
	}
	
	//Test scenario when there is a successful job schedule on addpath and httpclient returns 200 status
	@SuppressWarnings("unchecked")
	@Test
	public void addPathSuccessfulScheduleStatus200SingleCall() {
		
		try {
			//Start the quartz scheduler
			mockScheduler.start();
			
			//Excpect scheduling call on the slingscheduler and delegate on to the anonymous class created above 
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));	
			expectLastCall().andDelegateTo(delegatorScheduler);
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader("CQ-Action", "Delete");
			expectLastCall();
			
			get.setRequestHeader("CQ-Handle", "sample path");
			expectLastCall();
			
			get.setRequestHeader("CQ-Path", "sample path");
			expectLastCall();
			
			expect(httpClient.executeMethod(get)).andReturn(200);
			
			//Look into sequential restrictions for expectations of method execution
			get.releaseConnection();
			expectLastCall();
			
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
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	//Test scenario when there is a successful job schedule on addpath and httpclient returns other status
	//Logic is the same as above except the httpclient returns a different status than 200
	@SuppressWarnings("unchecked")
	@Test
	public void addPathSuccessfulScheduleStatusOtherSingleCall() {
		
		try {
			mockScheduler.start();
			
			//Setup basic initial expected behavior on relevant mocks
			
			
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));
			//We can expect the job to be rescheduled multiple times since the path hasn't been validated
			expectLastCall().andDelegateTo(delegatorScheduler).anyTimes();
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader(anyObject(String.class), anyObject(String.class));
			expectLastCall().times(3);
			
			get.releaseConnection();
			expectLastCall();
			
			expect(httpClient.executeMethod(get)).andReturn(404);
			
			replayAll();
			
			invalidator.init();
			invalidator.addPath("sample path");
			Thread.sleep(2000);
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			Set<String> paths = (Set<String>) pathsField.get(invalidator);
			
			assertFalse(paths.isEmpty());
			
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	//Test scenario when there is a successful job schedule on addpath and httpclient throws an exception
	//Same logic as above except the result of an exception
	@SuppressWarnings("unchecked")
	@Test
	public void addPathSuccessfulScheduleStatusExceptionSingleCall() {
		
		try {
			mockScheduler.start();
			
			
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));	
			expectLastCall().andDelegateTo(delegatorScheduler).anyTimes();
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader(anyObject(String.class), anyObject(String.class));
			expectLastCall().times(3);
			
			get.releaseConnection();
			expectLastCall();
			
			expect(httpClient.executeMethod(get)).andThrow(new RuntimeException("THIS SHOULD BE CAUGHT BY THE INVALIDATOR"));
			
			replayAll();
			
			invalidator.init();
			invalidator.addPath("sample path");
			Thread.sleep(2000);
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			Set<String> paths = (Set<String>) pathsField.get(invalidator);
			
			assertFalse(paths.isEmpty());
			
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	
	//Test scenario when scheduling a task on addpath throws an exception
	//We don't get as far as invalidate because an exception is thrown and the class goes straight to execute before any path is added
	//To the paths
	@SuppressWarnings("unchecked")
	@Test
	public void addPathUnsuccessfulSchedule() {
		
		try {
			mockScheduler.start();
			
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));	
			expectLastCall().andThrow(new RuntimeException()).anyTimes();
			
			replayAll();
			
			invalidator.init();
			invalidator.addPath("sample path");
			
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			Set<String> paths = (Set<String>) pathsField.get(invalidator);
			
			assertFalse(paths.isEmpty());
			
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	
	@Test
	public void deactivatePathsEmpty() {
		
		try {
			mockScheduler.start();
			
			//Setup basic initial expected behavior on relevant mocks
			
			scheduler.removeJob(jobName);
			expectLastCall();
			
			
			
			replayAll();
			
			invalidator.init();
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			pathsField.set(invalidator, new HashSet<String>());
			
			invalidator.deactivate();
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	
	@Test
	public void deactivatePathsHaveMembersStatus200() {
		
		try {
			mockScheduler.start();
			
			//Setup basic initial expected behavior on relevant mocks
			
			scheduler.removeJob(jobName);
			expectLastCall();
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader("CQ-Action", "Delete");
			expectLastCall();
			
			get.setRequestHeader("CQ-Handle", "sample path");
			expectLastCall();
			
			get.setRequestHeader("CQ-Path", "sample path");
			expectLastCall();
			
			expect(httpClient.executeMethod(get)).andReturn(200);
			
			get.releaseConnection();
			expectLastCall();
			
			replayAll();
			
			invalidator.init();
			
			Set<String> pSet = new HashSet<String>();
			pSet.add("sample path");
			
			Field pathsField = invalidator.getClass().getDeclaredField("paths");
			pathsField.setAccessible(true);
			pathsField.set(invalidator, pSet);
			
			invalidator.deactivate();
			
			@SuppressWarnings("unchecked")
			Set<String> paths = (Set<String>) pathsField.get(invalidator);
			assertTrue(paths.isEmpty());
			
			verifyAll();
			
			mockScheduler.shutdown();
			
		} catch (SchedulerException e) {
			fail("Unable to start mock scheduler");
		} catch (Exception e) {
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	
	
	
	//Test scenario when we have a successful schedule the return status for httpclient is 200 and we have Race conditions
	@SuppressWarnings("unchecked")
	@Test
	public void addPathSuccessfulScheduleStatus200RaceConditions() {
		
		try {
			mockScheduler.start();
			
			scheduler.fireJobAt(eq(jobName), eq(invalidator), (Map<String, Serializable>) eq(null), anyObject(Date.class));	
			expectLastCall().andDelegateTo(delegatorScheduler);
			
			expectNew(GetMethod.class, flushURI).andReturn(get);
			
			get.setRequestHeader(anyObject(String.class), anyObject(String.class));
			expectLastCall().times(15);
			
			get.releaseConnection();
			expectLastCall().times(5);
			
			expect(httpClient.executeMethod(get)).andReturn(200).times(5);
			
			replayAll();
			
			invalidator.init();
			invalidator.addPath("sample path");
			
			//We use the quartz scheduler to simulate four calls after the first one  but before the scheduled invalidate job to create race conditions
			mockScheduler.scheduleJob(newJob(CallPathJob.class).build() , newTrigger().startNow().build());
			mockScheduler.scheduleJob(newJob(CallPathJob.class).build() , newTrigger().startNow().build());
			mockScheduler.scheduleJob(newJob(CallPathJob.class).build() , newTrigger().startNow().build());
			mockScheduler.scheduleJob(newJob(CallPathJob.class).build() , newTrigger().startNow().build());
			
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
			fail("Generic expception fail mesage is: " + e.getMessage());
		}
	}
	
	
	

}

