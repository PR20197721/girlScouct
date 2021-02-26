package org.girlscouts.common.exception;

public class GirlScoutsException extends Exception {
    private static final long serialVersionUID = -7478714058587572283L;

    private Exception exception;
    private String reason;
    private String typeOfException;
    
    public GirlScoutsException(Exception e, String reason) {
    	super(reason, e);
    	this.exception = e;
    	this.reason = reason;
    }

    //This is created explicitly for TrashcanUtil and GSTrashcanServlet
    public GirlScoutsException(Exception e, String reason, String typeOfException) {
        super(reason, e);
        this.exception = e;
        this.reason = reason;
        this.typeOfException = typeOfException;
    }
    
    public Exception getException() {
	return exception;
    }
    
    public String getReason() {
    	return reason;
    }

    public String getTypeOfException() {
        return typeOfException;
    }
    

}
