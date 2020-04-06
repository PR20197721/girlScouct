package org.girlscouts.common.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

 

/**
 * 
 * @author Kiran.Boregowda
 *
 */

 

@ObjectClassDefinition(name = "Girl Scouts Replication Queue Monitor Configuration", description = "Replication Queue Monitor Configuration")
public @interface GirlscoutsReplicationQueueMonitorConfiguration {

 

    /**
     * schedulerName
     * 
     * @return String name
     */
    @AttributeDefinition(name = "Replication Queue Monitor Scheduler", description = "Replication Queue Monitor Scheduler", type = AttributeType.STRING)
    public String schedulerName() default "Replication Queue Monitor Scheduler";

 

    /**
     * serviceEnabled
     * 
     * @return serviceEnabled
     */
    @AttributeDefinition(name = "Enabled", description = "Enable Replication Queue Monitor Scheduler", type = AttributeType.BOOLEAN)
    boolean serviceEnabled() default false;

 

    /**
     * schedulerExpression
     * 
     * @return schedulerExpression
     */
    @AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run every day.", type = AttributeType.STRING)
    String schedulerExpression() default "0 0 0 1/1 * ? *";

 

    /**
     * sendNotifications
     * 
     * @return sendNotifications
     */
    @AttributeDefinition(name = "Send notifications", description = "Send notifications to recipents", type = AttributeType.BOOLEAN)
    boolean sendNotifications();

 

    /**
     * emailAddresses
     * 
     * @return emailAddresses
     */
    @AttributeDefinition(name = "Email Addresses", description = "Replication Queue Monitor recipents")
    String[] emailAddresses();
    
    /**
     * 
     * @return scheduler_concurrent
     */
    @AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
    boolean schedulerConcurrent() default true;
    
    @AttributeDefinition(name = "Max Time", description = "Max wait time in Minute", type = AttributeType.INTEGER)
    int maxTime() default 1;

 

}