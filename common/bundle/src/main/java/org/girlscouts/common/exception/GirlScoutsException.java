package org.girlscouts.common.exception;

public class GirlScoutsException extends Exception {
    private static final long serialVersionUID = -7478714058587572283L;

    private Exception exception;
    private String reason;
    
    public GirlScoutsException(Exception e, String reason) {
    	super(reason, e);
    	this.exception = e;
    	this.reason = reason;
    }
    
    public Exception getException() {
	return exception;
    }
    
    public String getReason() {
    	return reason;
    }
    

}
