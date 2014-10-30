<%@page import="java.security.MessageDigest" %>
<%


/*
String plainText = "text" + "salt";

MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
byte[] hash = messageDigest.digest( plainText.getBytes() );

out.println("Result: " + new String(hash));
*/
    	String password = "123456";
 
        MessageDigest md = MessageDigest.getInstance("SHA-512");//SHA-256");
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
   out.println("<br/>Hex format : " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<byteData.length;i++) {
    		String hex=Integer.toHexString(0xff & byteData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
    	}
    	out.println("<br/>Hex format : " + hexString.toString());


%>